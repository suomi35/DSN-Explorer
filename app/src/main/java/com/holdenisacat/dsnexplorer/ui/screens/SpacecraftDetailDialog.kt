package com.holdenisacat.dsnexplorer.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.PreviewMock
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.ui.components.NoSignalView
import com.holdenisacat.dsnexplorer.ui.components.SignalView
import com.holdenisacat.dsnexplorer.util.SiteFlags

/**
 * Spacecraft detail dialog: hero image, antenna context, and live signals.
 */
@Composable
fun SpacecraftDetailDialog(
    dish: Dish,
    spacecraft: Spacecraft,
    onDishClick: (Dish) -> Unit,
    onOpenExternal: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onDismiss) {
        val scrollState = rememberScrollState()

        Card(modifier = modifier) {
            Box {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenExternal(spacecraft.friendlyName) },
                    model = spacecraft.imageUrl(),
                    contentDescription = stringResource(R.string.spacecraft_image, spacecraft.friendlyName),
                )
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp, bottom = 8.dp)
                        .align(Alignment.BottomStart),
                    text = spacecraft.friendlyName,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 24.sp,
                        shadow = Shadow(color = Color.Black, blurRadius = 3f),
                    ),
                )
                Icon(
                    painter = painterResource(R.drawable.ic_open_in_new),
                    contentDescription = stringResource(R.string.open_in_browser),
                    modifier = Modifier
                        .padding(top = 8.dp, end = 8.dp)
                        .align(Alignment.TopEnd),
                )
            }

            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onDishClick(dish) },
                    text = stringResource(R.string.spacecraft_detail_antenna_name, dish.friendlyName),
                    fontSize = 18.sp,
                )
                Image(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    painter = painterResource(SiteFlags.flagFor(dish.siteName)),
                    contentDescription = stringResource(R.string.complex_flag, dish.siteName),
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(stringResource(R.string.active_dish_activity, dish.activity))
            }
            Spacer(modifier = Modifier.size(4.dp))
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(stringResource(R.string.mode, dish.mode))
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.active_dish_azimuth, dish.azimuthAngle))
            }
            Spacer(modifier = Modifier.size(4.dp))
            Row(modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(stringResource(R.string.active_dish_windspeed, dish.windSpeed))
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.active_dish_elevation, dish.elevationAngle))
            }
            Spacer(Modifier.height(10.dp))
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                if (dish.activeSignalsFor(spacecraft).isEmpty()) {
                    NoSignalView()
                } else {
                    dish.signalsFor(spacecraft).forEach { signal -> SignalView(signal) }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SpacecraftDetailDialogPreview() {
    SpacecraftDetailDialog(
        dish = PreviewMock.dish(),
        spacecraft = PreviewMock.spacecraft(),
        onDishClick = {},
        onOpenExternal = {},
        onDismiss = {},
    )
}
