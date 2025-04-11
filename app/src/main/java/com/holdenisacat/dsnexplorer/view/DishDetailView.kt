package com.holdenisacat.dsnexplorer.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.PreviewMock
import com.holdenisacat.dsnexplorer.model.SignalDirection
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.ui.component.AnimatedVectorDrawable
import com.holdenisacat.dsnexplorer.util.Constants
import com.holdenisacat.dsnexplorer.util.Utils

@Composable
fun DishDetailView(
    dish: Dish,
    onDishClick: (Dish) -> Unit,
    onSpacecraftClick: (Spacecraft) -> Unit,
    onDismissDetailView: () -> Unit,
    topRowDetailSpacing: Dp = 4.dp,
    horizontalPadding: Dp = 8.dp,
) {
    Dialog(onDismissRequest = { onDismissDetailView() }) {

        val scrollState = rememberScrollState()

        println(">>> dish.getDishImageUrl(): ${dish.getDishImageUrl()}")

        Card {

            Box {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onDishClick(dish)
                        },
                    model = dish.getDishImageUrl(),
                    contentDescription = "spacecraft image",
                )
                IconButton(onClick = { onDismissDetailView() }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "dismiss detail screen"
                    )
                }
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.BottomStart),
                    text = dish.friendlyName,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 24.sp,
                        shadow = Shadow(
                            color = Color.Black,
                            blurRadius = 3f
                        )
                    )
                )
            }

            Column(
                modifier = Modifier
                    .padding(6.dp)
            ) {
                Row {
                    Text(
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f),
                        text = stringResource(
                            R.string.dish_size,
                            dish.type
                        )
                    )
                    Text(
                        modifier = Modifier
                            .padding(2.dp),
                        textAlign = TextAlign.End,
                        text = stringResource(
                            R.string.spacecraft_detail_antenna_azimuth,
                            dish.azimuthAngle
                        )
                    )
                }
                Row {
                    Text(
                        modifier = Modifier
                            .padding(2.dp)
                            .weight(1f),
                        text = stringResource(
                            R.string.spacecraft_detail_antenna_windspeed,
                            dish.windSpeed
                        )
                    )
                    Text(
                        modifier = Modifier
                            .padding(2.dp),
                        textAlign = TextAlign.End,
                        text = stringResource(
                            R.string.spacecraft_detail_antenna_elevation,
                            dish.elevationAngle
                        )
                    )
                }

                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                ) {
                    dish.spacecraftList.forEach { spacecraft ->
                        Row(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .clickable {
                                    onSpacecraftClick(spacecraft)
                                }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_my_location),
                                tint = MaterialTheme.colorScheme.secondary,
                                contentDescription = "target icon",
                                modifier = Modifier
                                    .padding(1.dp)
                            )
                            Text(
                                modifier = Modifier
                                    .padding(4.dp),
                                text = spacecraft.friendlyName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(Modifier.weight(1f))
                            dish.getSpacecraftSignalsByDirection(spacecraft, SignalDirection.UP)
                                .forEach { upSignal ->
                                    when (upSignal.signalType) {
                                        Constants.DATA_SIGNAL -> {
                                            AnimatedVectorDrawable(R.drawable.uplink_data_animation)
                                        }

                                        Constants.CARRIER_SIGNAL -> {
                                            AnimatedVectorDrawable(R.drawable.uplink_carrier_animation)
                                        }
                                    }
                                }
                            dish.getSpacecraftSignalsByDirection(spacecraft, SignalDirection.DOWN)
                                .forEach { downSignal ->
                                    when (downSignal.signalType) {
                                        Constants.DATA_SIGNAL -> {
                                            AnimatedVectorDrawable(R.drawable.downlink_data_animation)
                                        }

                                        Constants.CARRIER_SIGNAL -> {
                                            AnimatedVectorDrawable(R.drawable.downlink_carrier_animation)
                                        }
                                    }
                                }
                        }
                        Row {
                            Text(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .align(Alignment.CenterVertically),
                                text = stringResource(
                                    R.string.range,
                                    Utils.distanceToString(spacecraft.uplegRange)
                                )
                            )
                            Text(
                                modifier = Modifier
                                    .padding(2.dp)
                                    .weight(1f)
                                    .align(Alignment.CenterVertically),
                                textAlign = TextAlign.End,
                                text = stringResource(
                                    R.string.roundtrip_light_time,
                                    Utils.lightTimeToString(spacecraft.roundtripLightTime)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DishDetailViewPreview() {
    DishDetailView(
        PreviewMock.dish(),
        onDishClick = {},
        onDismissDetailView = {},
        onSpacecraftClick = {}
    )
}