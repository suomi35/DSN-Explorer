package com.holdenisacat.dsnexplorer.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.SignalDirection
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.ui.component.AnimatedVectorDrawable
import com.holdenisacat.dsnexplorer.util.Constants

@Composable
fun SpacecraftTargetRow(
    spacecraft: Spacecraft,
    dish: Dish,
    modifier: Modifier = Modifier,
    nameFontSize: TextUnit = 18.sp,
    leadingIcon: @Composable () -> Unit = {
        Icon(
            painter = painterResource(id = R.drawable.ic_satellite),
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = "satellite icon",
            modifier = Modifier.padding(1.dp),
        )
    },
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon()
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            text = spacecraft.friendlyName,
            fontWeight = FontWeight.Bold,
            fontSize = nameFontSize,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        SpacecraftSignalIcons(dish = dish, spacecraft = spacecraft)
    }
}

@Composable
fun SpacecraftSignalIcons(
    dish: Dish,
    spacecraft: Spacecraft,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        dish.getSpacecraftSignalsByDirection(spacecraft, SignalDirection.UP).forEach { upSignal ->
            when (upSignal.signalType) {
                Constants.DATA_SIGNAL -> AnimatedVectorDrawable(R.drawable.uplink_data_animation)
                Constants.CARRIER_SIGNAL -> AnimatedVectorDrawable(R.drawable.uplink_carrier_animation)
            }
        }
        dish.getSpacecraftSignalsByDirection(spacecraft, SignalDirection.DOWN).forEach { downSignal ->
            when (downSignal.signalType) {
                Constants.DATA_SIGNAL -> AnimatedVectorDrawable(R.drawable.downlink_data_animation)
                Constants.CARRIER_SIGNAL -> AnimatedVectorDrawable(R.drawable.downlink_carrier_animation)
            }
        }
    }
}
