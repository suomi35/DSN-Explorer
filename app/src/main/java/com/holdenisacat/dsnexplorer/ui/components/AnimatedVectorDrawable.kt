package com.holdenisacat.dsnexplorer.ui.components

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun AnimatedVectorDrawable(
    vectorDrawable: Int,
    modifier: Modifier = Modifier,
) {
    val image = AnimatedImageVector.animatedVectorResource(vectorDrawable)
    var loopGeneration by remember(image) { mutableIntStateOf(0) }

    LaunchedEffect(image, loopGeneration) {
        delay(image.totalDuration.toLong())
        loopGeneration++
    }

    key(loopGeneration) {
        LoopingAvdFrame(image = image, modifier = modifier)
    }
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
private fun LoopingAvdFrame(
    image: AnimatedImageVector,
    modifier: Modifier = Modifier,
) {
    var atEnd by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        atEnd = true
    }

    Image(
        painter = rememberAnimatedVectorPainter(image, atEnd),
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Fit,
    )
}
