package com.holdenisacat.dsnexplorer.ui.component

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun AnimatedVectorDrawable(
    vectorDrawable: Int
) {
    val image = AnimatedImageVector.animatedVectorResource(vectorDrawable)
    var atEnd by remember { mutableStateOf(false) }
    var isRunning by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    suspend fun runAnimation() {
        while (isRunning) {
            delay(1000) // set here your delay between animations
            atEnd = !atEnd
        }
    }

    LaunchedEffect(image) {
        runAnimation()
    }

    Image(
        painter = rememberAnimatedVectorPainter(image, atEnd),
        contentDescription = "Timer",
        modifier = Modifier.clickable {
            isRunning = !isRunning // start/stop animation
            if (isRunning) // run the animation if isRunning is true.
                scope.launch {
                    runAnimation()
                }
        },
        contentScale = ContentScale.Crop
    )
}