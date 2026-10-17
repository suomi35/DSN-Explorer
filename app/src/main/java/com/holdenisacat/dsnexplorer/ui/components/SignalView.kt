package com.holdenisacat.dsnexplorer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.PreviewMock
import com.holdenisacat.dsnexplorer.model.Signal
import com.holdenisacat.dsnexplorer.model.SignalDirection
import com.holdenisacat.dsnexplorer.model.SignalType
import com.holdenisacat.dsnexplorer.ui.components.AnimatedVectorDrawable
import com.holdenisacat.dsnexplorer.ui.theme.LocalCustomColorPalette
import com.holdenisacat.dsnexplorer.util.Formatters

/**
 * Header plus detail card for one uplink/downlink signal.
 */
@Composable
fun SignalView(signal: Signal, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .background(signal.headerColor())
                .padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                fontSize = 16.sp,
                text = signal.type.rawValue.replaceFirstChar { it.uppercase() },
            )
            when (signal.direction) {
                SignalDirection.UP -> Icon(
                    painter = painterResource(R.drawable.ic_double_arrow_up),
                    contentDescription = stringResource(R.string.uplink),
                    modifier = Modifier.padding(start = 4.dp),
                )

                SignalDirection.DOWN -> Icon(
                    painter = painterResource(R.drawable.ic_double_arrow_down),
                    contentDescription = stringResource(R.string.downlink),
                    modifier = Modifier.padding(start = 4.dp),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier.padding(8.dp),
                contentAlignment = Alignment.Center,
            ) {
                signal.animationRes()?.let { AnimatedVectorDrawable(it) }
            }
        }
        Row(
            modifier = Modifier.background(signal.containerColor()),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Column(
                modifier = Modifier
                    .padding(6.dp)
                    .weight(1f),
            ) {
                Text(
                    modifier = Modifier.padding(2.dp),
                    text = stringResource(
                        R.string.signal_detail_frequency,
                        Formatters.frequency(signal.frequency),
                    ),
                )
                Text(
                    modifier = Modifier.padding(2.dp),
                    text = stringResource(
                        R.string.signal_detail_data_rate,
                        Formatters.dataRate(signal.dataRate),
                    ),
                )
            }
            Column(
                modifier = Modifier
                    .padding(6.dp)
                    .weight(1f),
            ) {
                Text(
                    modifier = Modifier
                        .padding(2.dp)
                        .align(Alignment.End),
                    text = stringResource(R.string.band, signal.band),
                )
                Text(
                    modifier = Modifier
                        .padding(2.dp)
                        .align(Alignment.End),
                    text = stringResource(
                        when (signal.direction) {
                            SignalDirection.UP -> R.string.signal_detail_transmitted
                            SignalDirection.DOWN -> R.string.signal_detail_received
                        },
                        Formatters.power(signal.power),
                    ),
                )
            }
        }
    }
}

@Composable
private fun Signal.headerColor(): Color {
    val palette = LocalCustomColorPalette.current
    return when (type to direction) {
        SignalType.CARRIER to SignalDirection.UP -> palette.carrierUpHeader
        SignalType.CARRIER to SignalDirection.DOWN -> palette.carrierDownHeader
        SignalType.DATA to SignalDirection.UP -> palette.dataUpHeader
        SignalType.DATA to SignalDirection.DOWN -> palette.dataDownHeader
        SignalType.RANGING to SignalDirection.UP -> palette.rangingHeader
        else -> MaterialTheme.colorScheme.error
    }
}

@Composable
private fun Signal.containerColor(): Color {
    val palette = LocalCustomColorPalette.current
    return when (type to direction) {
        SignalType.CARRIER to SignalDirection.UP -> palette.carrierUpContainer
        SignalType.CARRIER to SignalDirection.DOWN -> palette.carrierDownContainer
        SignalType.DATA to SignalDirection.UP -> palette.dataUpContainer
        SignalType.DATA to SignalDirection.DOWN -> palette.dataDownContainer
        SignalType.RANGING to SignalDirection.UP -> palette.rangingContainer
        else -> MaterialTheme.colorScheme.error
    }
}

private fun Signal.animationRes(): Int? = when (type to direction) {
    SignalType.CARRIER to SignalDirection.UP -> R.drawable.uplink_carrier_animation
    SignalType.CARRIER to SignalDirection.DOWN -> R.drawable.downlink_carrier_animation
    SignalType.DATA to SignalDirection.UP -> R.drawable.uplink_data_animation
    SignalType.DATA to SignalDirection.DOWN -> R.drawable.downlink_data_animation
    else -> null
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SignalViewPreview() {
    SignalView(PreviewMock.signalUp())
}
