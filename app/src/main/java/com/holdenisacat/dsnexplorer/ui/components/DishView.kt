package com.holdenisacat.dsnexplorer.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.PreviewMock
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.ui.theme.LocalCustomColorPalette

/**
 * Summary card for one antenna: header with pointing data, then its target list.
 */
@Composable
fun DishView(
    dish: Dish,
    onDishClick: (Dish) -> Unit,
    onSpacecraftClick: (Spacecraft) -> Unit,
    modifier: Modifier = Modifier,
    topRowDetailSpacing: Dp = 10.dp,
    dishNameFontSize: TextUnit = 20.sp,
) {
    val contentColor = if (dish.isActive) {
        MaterialTheme.colorScheme.onSurface
    } else {
        LocalCustomColorPalette.current.inactiveDishText
    }

    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_dish),
                    tint = if (dish.isActive) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        LocalCustomColorPalette.current.inactiveDishText
                    },
                    contentDescription = stringResource(R.string.dish_icon),
                    modifier = Modifier.padding(1.dp),
                )
                Text(
                    modifier = Modifier
                        .padding(2.dp)
                        .weight(1f)
                        .clickable { onDishClick(dish) },
                    text = dish.friendlyName,
                    fontWeight = FontWeight.Bold,
                    fontSize = dishNameFontSize,
                    color = contentColor,
                )
                if (dish.isActive) {
                    DishPointingDetails(
                        dish = dish,
                        topRowDetailSpacing = topRowDetailSpacing,
                    )
                } else {
                    Text(
                        modifier = Modifier.padding(2.dp),
                        textAlign = TextAlign.End,
                        text = stringResource(R.string.dish_inactive),
                        color = contentColor,
                    )
                }
            }
            if (dish.isActive) {
                Spacer(modifier = Modifier.size(4.dp))
                dish.spacecraftList.forEach { spacecraft ->
                    SpacecraftTargetRow(
                        spacecraft = spacecraft,
                        dish = dish,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable { onSpacecraftClick(spacecraft) },
                    )
                    RangeRow(spacecraft = spacecraft)
                }
            }
        }
    }
}

@Composable
private fun DishPointingDetails(dish: Dish, topRowDetailSpacing: Dp) {
    Row {
        Text(
            modifier = Modifier.padding(2.dp),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.active_dish_azimuth, dish.azimuthAngle),
        )
        Spacer(modifier = Modifier.size(topRowDetailSpacing))
        Text(
            modifier = Modifier.padding(2.dp),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.active_dish_elevation, dish.elevationAngle),
        )
    }
}

@Preview
@Composable
private fun DishViewActivePreview() {
    DishView(PreviewMock.dish(isActive = true), onDishClick = {}, onSpacecraftClick = {})
}

@Preview
@Composable
private fun DishViewInactivePreview() {
    DishView(PreviewMock.dish(isActive = false), onDishClick = {}, onSpacecraftClick = {})
}
