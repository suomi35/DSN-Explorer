package com.holdenisacat.dsnexplorer.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Signal-type colors that complement [androidx.compose.material3.MaterialTheme].
 *
 * Material's color scheme cannot express the per-direction/per-type tints the
 * signal cards need, so they live here and are provided via a CompositionLocal.
 */
@Immutable
data class CustomColorPalette(
    val carrierUpHeader: Color = Color.Unspecified,
    val carrierDownHeader: Color = Color.Unspecified,
    val dataUpHeader: Color = Color.Unspecified,
    val dataDownHeader: Color = Color.Unspecified,
    val rangingHeader: Color = Color.Unspecified,
    val carrierUpContainer: Color = Color.Unspecified,
    val carrierDownContainer: Color = Color.Unspecified,
    val dataUpContainer: Color = Color.Unspecified,
    val dataDownContainer: Color = Color.Unspecified,
    val rangingContainer: Color = Color.Unspecified,
    val noSignalHeader: Color = Color.Unspecified,
    val noSignalContainer: Color = Color.Unspecified,
    val inactiveDishText: Color = Color.Unspecified,
    val noSpacecraftFound: Color = Color.Unspecified,
)

val LightCustomColorPalette = CustomColorPalette(
    carrierUpHeader = Color(0xFFFFFBD2),
    carrierDownHeader = Color(0xFFDCF6DC),
    dataUpHeader = Color(0xFFFFD7BA),
    dataDownHeader = Color(0xFFDAEAF5),
    rangingHeader = Color(0xFFFFD6DA),
    carrierUpContainer = Color(0xFFFFFEF5),
    carrierDownContainer = Color(0xFFF7FDF7),
    dataUpContainer = Color(0xFFFFF6F0),
    dataDownContainer = Color(0xFFF7FAFD),
    rangingContainer = Color(0xFFFFF6F7),
    noSignalHeader = Color(0xFF745757),
    noSignalContainer = Color(0xFF8A5A5A),
    inactiveDishText = Color(0xFF4F5153),
    noSpacecraftFound = Color(0xFF4F5153),
)

val DarkCustomColorPalette = CustomColorPalette(
    carrierUpHeader = Color(0xFFFFFBD2),
    carrierDownHeader = Color(0xFFDCF6DC),
    dataUpHeader = Color(0xFF7B6858),
    dataDownHeader = Color(0xFF646F7A),
    rangingHeader = Color(0xFFFFD6DA),
    carrierUpContainer = Color(0xFFFFFEF5),
    carrierDownContainer = Color(0xFFF7FDF7),
    dataUpContainer = Color(0xFF927659),
    dataDownContainer = Color(0xFF667A90),
    rangingContainer = Color(0xFFFFF6F7),
    noSignalHeader = Color(0xFF745757),
    noSignalContainer = Color(0xFF8A5A5A),
    inactiveDishText = Color(0xFF4F5153),
    noSpacecraftFound = Color(0xFFBBBBBB),
)

val LocalCustomColorPalette = staticCompositionLocalOf { CustomColorPalette() }
