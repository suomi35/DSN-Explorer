package com.holdenisacat.dsnexplorer.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

/**
 * Site-local time shown in the top app bar.
 */
@Composable
fun SiteTime(
    datetime: String,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = datetime,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        minLines = 2,
        textAlign = TextAlign.End,
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun SiteTimePreview() {
    SiteTime(datetime = "APR 07\n16:54")
}
