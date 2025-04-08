package com.holdenisacat.dsnexplorer.view

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.PreviewMock
import com.holdenisacat.dsnexplorer.model.Signal
import com.holdenisacat.dsnexplorer.model.SignalDirection
import com.holdenisacat.dsnexplorer.ui.component.AnimatedVectorDrawable
import com.holdenisacat.dsnexplorer.ui.theme.LocalCustomColorPalette
import com.holdenisacat.dsnexplorer.util.Constants
import com.holdenisacat.dsnexplorer.util.Utils
import java.util.Locale

@Composable
fun SignalView(
    signal: Signal
) {

    Column {
        Row(
            modifier = Modifier
                .background(
                    when {
                        signal.direction == SignalDirection.UP
                                && signal.signalType == Constants.CARRIER_SIGNAL ->
                            LocalCustomColorPalette.current.backgroundDataTypeCarrierUpHeader
                        signal.direction == SignalDirection.DOWN
                                && signal.signalType == Constants.CARRIER_SIGNAL ->
                            LocalCustomColorPalette.current.backgroundDataTypeCarrierDownHeader
                        signal.direction == SignalDirection.UP
                                && signal.signalType == Constants.DATA_SIGNAL ->
                            LocalCustomColorPalette.current.backgroundDataTypeDataUpHeader
                        signal.direction == SignalDirection.DOWN
                                && signal.signalType == Constants.DATA_SIGNAL ->
                            LocalCustomColorPalette.current.backgroundDataTypeDataDownHeader
                        signal.signalType == Constants.RANGING_SIGNAL ->
                            LocalCustomColorPalette.current.backgroundDataTypeRangingUpHeader
                        else -> MaterialTheme.colorScheme.error
                    }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(Alignment.CenterVertically),
                fontSize = 16.sp,
                text = when {
                    signal.direction == SignalDirection.UP -> {
                        signal.signalType.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                        }
                    }
                    else -> {
                        signal.signalType.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
                        }
                    }
                }
            )
            when {
                signal.direction == SignalDirection.UP -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_double_arrow_up),
                        contentDescription = "up icon",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }

                else -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_double_arrow_down),
                        contentDescription = "down icon",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterVertically),
            ) {
                when {
                    signal.direction == SignalDirection.UP
                            && signal.signalType == Constants.CARRIER_SIGNAL ->
                        AnimatedVectorDrawable(R.drawable.uplink_carrier_animation)

                    signal.direction == SignalDirection.DOWN
                            && signal.signalType == Constants.CARRIER_SIGNAL ->
                        AnimatedVectorDrawable(R.drawable.downlink_carrier_animation)

                    signal.direction == SignalDirection.UP
                            && signal.signalType == Constants.DATA_SIGNAL ->
                        AnimatedVectorDrawable(R.drawable.uplink_data_animation)

                    signal.direction == SignalDirection.DOWN
                            && signal.signalType == Constants.DATA_SIGNAL ->
                        AnimatedVectorDrawable(R.drawable.downlink_data_animation)
                }
            }
        }
        Row(
            modifier = Modifier
                .background(
                    when {
                        signal.direction == SignalDirection.UP
                                && signal.signalType == Constants.CARRIER_SIGNAL ->
                            LocalCustomColorPalette.current.backgroundDataTypeCarrierUpContainer
                        signal.direction == SignalDirection.DOWN
                                && signal.signalType == Constants.CARRIER_SIGNAL ->
                            LocalCustomColorPalette.current.backgroundDataTypeCarrierDownContainer
                        signal.direction == SignalDirection.UP
                                && signal.signalType == Constants.DATA_SIGNAL ->
                            LocalCustomColorPalette.current.backgroundDataTypeDataUpContainer
                        signal.direction == SignalDirection.DOWN
                                && signal.signalType == Constants.DATA_SIGNAL ->
                            LocalCustomColorPalette.current.backgroundDataTypeDataDownContainer
                        signal.signalType == Constants.RANGING_SIGNAL ->
                            LocalCustomColorPalette.current.backgroundDataTypeRangingUpContainer
                        else -> MaterialTheme.colorScheme.error
                    }
                )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(2.dp),
                        text = stringResource(R.string.signal_detail_frequency,
                            Utils.frequencyToString(signal.frequency)
                        )
                    )
                    Text(
                        modifier = Modifier
                            .padding(2.dp),
                        text = stringResource(R.string.signal_detail_data_rate,
                            Utils.dataRateToString(signal.dataRate)
                        )
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(6.dp)
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(2.dp)
                            .align(Alignment.End),
                        text = stringResource(R.string.band, signal.band)
                    )
                    Text(
                        modifier = Modifier
                            .padding(2.dp)
                            .align(Alignment.End),
                        text = when (signal.direction) {
                            SignalDirection.UP -> {
                                stringResource(R.string.signal_detail_transmitted,
                                    Utils.powerToString(signal.power))
                            }
                            SignalDirection.DOWN -> {
                                stringResource(R.string.signal_detail_received,
                                    Utils.powerToString(signal.power))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview (showBackground = true, backgroundColor = 0xffffffff)
@Composable
fun SignalViewPreview() {
    SignalView(
        PreviewMock.signalUp()
    )
}