package com.holdenisacat.dsnexplorer.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.PreviewMock
import com.holdenisacat.dsnexplorer.model.Spacecraft
import com.holdenisacat.dsnexplorer.ui.components.RangeRow
import com.holdenisacat.dsnexplorer.ui.theme.LocalCustomColorPalette

/**
 * Alphabetical list of every spacecraft currently tracked by the DSN.
 */
@Composable
fun SpacecraftScreen(
    spacecraft: List<Spacecraft>,
    onSpacecraftClick: (Spacecraft) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (spacecraft.isEmpty()) {
        EmptySpacecraftState(modifier)
        return
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(spacecraft, key = { it.name }) { item ->
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .clickable { onSpacecraftClick(item) },
                            text = item.friendlyName,
                            fontSize = 18.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Image(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            painter = painterResource(item.flagRes),
                            contentDescription = stringResource(R.string.complex_flag, item.friendlyName),
                        )
                    }
                    RangeRow(spacecraft = item)
                }
            }
        }
    }
}

@Composable
private fun EmptySpacecraftState(modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                modifier = Modifier.padding(16.dp),
                colorFilter = ColorFilter.tint(LocalCustomColorPalette.current.noSpacecraftFound),
                painter = painterResource(R.drawable.ic_half_moon),
                contentDescription = null,
            )
            Text(
                modifier = Modifier.padding(2.dp),
                text = stringResource(R.string.no_active_spacecraft),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x353535)
@Composable
private fun SpacecraftScreenPreview() {
    SpacecraftScreen(PreviewMock.spacecraftList(), onSpacecraftClick = {})
}

@Preview(showBackground = true, backgroundColor = 0x353535)
@Composable
private fun SpacecraftScreenEmptyPreview() {
    SpacecraftScreen(emptyList(), onSpacecraftClick = {})
}
