package com.holdenisacat.dsnexplorer.ui.components

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.Dish
import com.holdenisacat.dsnexplorer.model.PreviewMock
import com.holdenisacat.dsnexplorer.model.SignalDirection
import com.holdenisacat.dsnexplorer.model.SignalType
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.ui.components.AnimatedVectorDrawable

/**
 * One row in a dish's target list: spacecraft name followed by animated
 * uplink/downlink signal indicators.
 */
@Composable
fun SpacecraftTargetRow(
    spacecraft: Spacecraft,
    dish: Dish,
    modifier: Modifier = Modifier,
    nameFontSize: TextUnit = 18.sp,
    leadingIcon: @Composable () -> Unit = {
        Icon(
            painter = painterResource(R.drawable.ic_satellite),
            tint = MaterialTheme.colorScheme.secondary,
            contentDescription = stringResource(R.string.satellite_icon),
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
private fun SpacecraftSignalIcons(dish: Dish, spacecraft: Spacecraft) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        dish.signalsFor(spacecraft, SignalDirection.UP).forEach { signal ->
            when (signal.type) {
                SignalType.DATA -> AnimatedVectorDrawable(R.drawable.uplink_data_animation)
                SignalType.CARRIER -> AnimatedVectorDrawable(R.drawable.uplink_carrier_animation)
                else -> Unit
            }
        }
        dish.signalsFor(spacecraft, SignalDirection.DOWN).forEach { signal ->
            when (signal.type) {
                SignalType.DATA -> AnimatedVectorDrawable(R.drawable.downlink_data_animation)
                SignalType.CARRIER -> AnimatedVectorDrawable(R.drawable.downlink_carrier_animation)
                else -> Unit
            }
        }
    }
}

@Preview
@Composable
private fun SpacecraftTargetRowPreview() {
    SpacecraftTargetRow(
        spacecraft = PreviewMock.spacecraft(),
        dish = PreviewMock.dish(),
    )
}
