package com.holdenisacat.dsnexplorer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.PreviewMock
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.ui.components.RangeRow
import com.holdenisacat.dsnexplorer.ui.components.SpacecraftTargetRow

/**
 * Full antenna detail dialog with pointing data and target list.
 */
@Composable
fun DishDetailDialog(
    dish: Dish,
    onSpacecraftClick: (Spacecraft) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onDismiss) {
        val scrollState = rememberScrollState()

        Card(modifier = modifier) {
            Box {
                AsyncImage(
                    modifier = Modifier.fillMaxWidth(),
                    model = dish.imageUrl(),
                    contentDescription = stringResource(R.string.dish_image, dish.friendlyName),
                )
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomStart),
                    text = dish.friendlyName,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 24.sp,
                        shadow = Shadow(color = Color.Black, blurRadius = 3f),
                    ),
                )
            }

            Column(modifier = Modifier.padding(6.dp)) {
                Row {
                    Text(
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f),
                        text = stringResource(R.string.dish_size, dish.type),
                    )
                    Text(
                        modifier = Modifier.padding(2.dp),
                        text = stringResource(R.string.active_dish_azimuth, dish.azimuthAngle),
                    )
                }
                Row {
                    Text(
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f),
                        text = stringResource(R.string.active_dish_windspeed, dish.windSpeed),
                    )
                    Text(
                        modifier = Modifier.padding(2.dp),
                        text = stringResource(R.string.active_dish_elevation, dish.elevationAngle),
                    )
                }

                Column(modifier = Modifier.verticalScroll(scrollState)) {
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
}

@Preview
@Composable
private fun DishDetailDialogPreview() {
    DishDetailDialog(PreviewMock.dish(), onSpacecraftClick = {}, onDismiss = {})
}
