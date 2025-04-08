package com.holdenisacat.dsnexplorer.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holdenisacat.dsnexplorer.R
import com.holdenisacat.dsnexplorer.model.PreviewMock
import com.holdenisacat.dsnexplorer.model.Target
import com.holdenisacat.dsnexplorer.util.Utils

@Composable
fun SpacecraftListView(
    targets: ArrayList<Target>,
    onTargetClick: (Target) -> Unit,
) {
    // this could be enhanced with ElevatedCard/OutlinedCard/FilledCard

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(targets) { target ->
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize()
                ) {
                    Row {
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .clickable {
                                    onTargetClick(target)
                                },
                            text = target.friendlyName,
                            fontSize = 18.sp
                        )
                        Image(
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            painter = painterResource(id = target.flag),
                            contentDescription = "target-observing antenna country flag image"
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(2.dp)
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            text = stringResource(
                                R.string.range,
                                Utils.distanceToString(target.uplegRange)
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
                                Utils.lightTimeToString(target.roundtripLightTime)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SpacecraftListViewPreview() {
    SpacecraftListView(
        PreviewMock.targets(),
        onTargetClick = {}
    )
}