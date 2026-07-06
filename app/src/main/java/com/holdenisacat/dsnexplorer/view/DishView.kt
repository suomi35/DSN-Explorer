package com.holdenisacat.dsnexplorer.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import com.holdenisacat.dsnexplorer.util.Utils

@Composable
fun DishView(
    dish: Dish,
    onDishClick: (Dish) -> Unit,
    onSpacecraftClick: (Spacecraft) -> Unit,
    topRowDetailSpacing: Dp = 10.dp,
    dishNameFontSize: TextUnit = 20.sp
) {
    // this could be enhanced with ElevatedCard/OutlinedCard/FilledCard
    when (dish.isActive) {
        true -> {
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(6.dp)
                ) {
                    Row(
//                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_dish),
                            tint = MaterialTheme.colorScheme.secondary,
                            contentDescription = "dish icon",
                            modifier = Modifier
                                .padding(1.dp)
                                .align(Alignment.CenterVertically),
                        )
                        Text(
                            modifier = Modifier
                                .padding(2.dp)
                                .weight(1f)
                                .align(Alignment.CenterVertically)
                                .clickable{ onDishClick(dish) },
                            text = dish.friendlyName,
                            fontWeight = FontWeight.Bold,
                            fontSize = dishNameFontSize,
                        )
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {

                            Text(
                                modifier = Modifier
                                    .padding(2.dp),
                                textAlign = TextAlign.End,
                                text = stringResource(
                                    R.string.active_dish_windspeed,
                                    dish.windSpeed
                                )
                            )
                            Row {
                                Text(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .align(Alignment.CenterVertically),
                                    textAlign = TextAlign.Center,
                                    text = stringResource(
                                        R.string.active_dish_azimuth,
                                        dish.azimuthAngle
                                    )
                                )
                                Spacer(modifier = Modifier.size(topRowDetailSpacing))
                                Text(
                                    modifier = Modifier
                                        .padding(2.dp)
                                        .align(Alignment.CenterVertically),
                                    textAlign = TextAlign.Center,
                                    text = stringResource(
                                        R.string.active_dish_elevation,
                                        dish.elevationAngle
                                    )
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    dish.spacecraftList.forEach { spacecraft ->
                        SpacecraftTargetRow(
                            spacecraft = spacecraft,
                            dish = dish,
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clickable { onSpacecraftClick(spacecraft) },
                        )
                        Row {
                            Text(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .align(Alignment.CenterVertically),
                                text = stringResource(R.string.range,
                                    Utils.distanceToString(spacecraft.uplegRange, spacecraft.rangeIsEstimated))
                            )
                            Text(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .weight(1f)
                                    .align(Alignment.CenterVertically),
                                textAlign = TextAlign.End,
                                text = stringResource(
                                    R.string.roundtrip_light_time,
                                    Utils.lightTimeToString(spacecraft.roundtripLightTime, spacecraft.rangeIsEstimated)
                                )
                            )
                        }
                    }
                }
            }
        }

        false -> {
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.onSurfaceVariant,
//                ),
            ) {
                Column(
                    modifier = Modifier
                        .padding(6.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_dish),
                            tint = LocalCustomColorPalette.current.inactiveDishText,
                            contentDescription = "dish icon",
                            modifier = Modifier
                                .padding(1.dp)
                                .align(Alignment.CenterVertically),
                        )
                        Text(
                            modifier = Modifier
                                .padding(2.dp)
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            text = dish.friendlyName,
                            fontWeight = FontWeight.Bold,
                            fontSize = dishNameFontSize,
                            color = LocalCustomColorPalette.current.inactiveDishText
                        )
                        Text(
                            modifier = Modifier
                                .padding(2.dp)
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            textAlign = TextAlign.End,
                            text = stringResource(R.string.dish_inactive),
                            color = LocalCustomColorPalette.current.inactiveDishText
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DishViewPreview() {
    DishView(
        PreviewMock.dish(isActive = false),
        onDishClick = {},
        onSpacecraftClick = {}
    )
}