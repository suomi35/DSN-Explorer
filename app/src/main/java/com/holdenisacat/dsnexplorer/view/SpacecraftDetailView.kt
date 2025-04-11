package com.holdenisacat.dsnexplorer.view

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.PreviewMock
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.util.Utils
import java.util.Locale

@Composable
fun SpacecraftDetailView(
    dish: Dish,
    spacecraft: Spacecraft,
    onDishClick: (Dish) -> Unit,
    onSpacecraftClick: (String) -> Unit,
    onDismissDetailView: () -> Unit,
    topRowDetailSpacing: Dp = 4.dp,
    horizontalPadding: Dp = 8.dp,
) {
    Dialog(onDismissRequest = { onDismissDetailView() }) {

        val scrollState = rememberScrollState()

        Card {

            Box {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSpacecraftClick(spacecraft.friendlyName)
                        },
                    model = stringResource(
                        R.string.spacecraft_image_url,
                        Uri.encode(spacecraft.friendlyName.lowercase(Locale.ROOT))
                    ),
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
                        .padding(start = 8.dp, bottom = 8.dp)
                        .align(Alignment.BottomStart),
                    text = spacecraft.friendlyName,
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
                Icon(
                    painter = painterResource(id = R.drawable.ic_open_in_new),
                    contentDescription = "open icon",
                    modifier = Modifier
                        .padding(top = 8.dp, end = 8.dp)
                        .align(Alignment.TopEnd),
                )
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding)
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onDishClick(dish)
                        },
                    text = stringResource(
                        R.string.spacecraft_detail_antenna_name,
                        dish.friendlyName
                    ),
                    fontSize = 18.sp
                )
                Image(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    painter = painterResource(id = Utils.getSiteFlag(dish.dishLocation)),
                    contentDescription = "${dish.dishLocation} image"
                )
            }
            Spacer(modifier = Modifier.size(topRowDetailSpacing))
            Row(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding)
            ) {
                Text(stringResource(R.string.active_dish_activity, dish.activity))
            }
            Spacer(modifier = Modifier.size(topRowDetailSpacing))
            Row(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding)
            ) {
                Text(stringResource(R.string.mode, dish.getDishMode()))
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.active_dish_azimuth, dish.azimuthAngle))
            }
            Spacer(modifier = Modifier.size(topRowDetailSpacing))
            Row(
                modifier = Modifier
                    .padding(horizontal = horizontalPadding)
            ) {
                Text(stringResource(R.string.active_dish_windspeed, dish.windSpeed))
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.active_dish_elevation, dish.elevationAngle))
            }
            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .verticalScroll(scrollState)
            ) {
                val targetActiveSignals = dish.getSpacecraftActiveSignals(spacecraft)
                when (targetActiveSignals.size > 0) {
                    true -> dish.getSpacecraftSignals(spacecraft).forEach { signal -> SignalView(signal) }
                    false -> NoSignalView()
                }
            }
        }

    }
}

@Preview
@Composable
fun SpacecraftDetailViewPreview() {
    SpacecraftDetailView(
        PreviewMock.dish(),
        PreviewMock.spacecraft(),
        onDishClick = {},
        onDismissDetailView = {},
        onSpacecraftClick = {}
    )
}