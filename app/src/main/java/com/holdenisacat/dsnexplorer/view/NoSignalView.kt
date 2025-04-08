package com.holdenisacat.dsnexplorer.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holdenisacat.dsnexplorer.ui.theme.LocalCustomColorPalette

@Composable
fun NoSignalView() {

    Column {
        Row(
            modifier = Modifier
                .background(LocalCustomColorPalette.current.backgroundNoSignalHeader),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                fontSize = 16.sp,
                text = "No Signals"
            )
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .background(LocalCustomColorPalette.current.backgroundNoSignalContainer)
                .padding(8.dp),
            text = "Status: Setup/Teardown/Tracking"
        )
    }
}

@Preview (showBackground = true, backgroundColor = 0xffffffff)
@Composable
fun NoSignalViewPreview() {
    NoSignalView()
}