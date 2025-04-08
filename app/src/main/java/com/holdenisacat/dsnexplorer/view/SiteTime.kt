package com.holdenisacat.dsnexplorer.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SiteTime(
    datetime: String
) {

    Column {
        Text(
            modifier = Modifier
                .padding(horizontal = 2.dp),
            text = datetime,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            minLines = 2,
            textAlign = TextAlign.End
        )
    }
}

@Preview (showBackground = true, backgroundColor = 0xffffffff)
@Composable
fun SiteTimePreview() {
    SiteTime(
        datetime = "APR 07\n16:54"
    )
}