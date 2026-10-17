package com.holdenisacat.dsnexplorer.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.PreviewMock
import com.holdenisacat.dsnexplorer.model.Spacecraft

/**
 * Range and round-trip light-time line shown under a spacecraft row.
 */
@Composable
fun RangeRow(spacecraft: Spacecraft, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier
                .padding(2.dp)
                .align(Alignment.CenterVertically),
            text = stringResource(R.string.range, spacecraft.formattedRange),
        )
        Text(
            modifier = Modifier
                .padding(2.dp)
                .weight(1f)
                .align(Alignment.CenterVertically),
            textAlign = TextAlign.End,
            text = stringResource(R.string.roundtrip_light_time, spacecraft.formattedRoundtripLightTime),
        )
    }
}

@Preview
@Composable
private fun RangeRowPreview() {
    RangeRow(PreviewMock.spacecraft())
}
