package com.holdenisacat.dsnexplorer.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CustomColorPalette(
    val infoWindowBackground: Color = Color.Unspecified,
    val batteryGreen: Color = Color.Unspecified,
    val batteryYellow: Color = Color.Unspecified,
    val batteryRed: Color = Color.Unspecified,
    val backgroundDataTypeCarrierUpHeader: Color = Color.Unspecified,
    val backgroundDataTypeCarrierDownHeader: Color = Color.Unspecified,
    val backgroundDataTypeDataUpHeader: Color = Color.Unspecified,
    val backgroundDataTypeDataDownHeader: Color = Color.Unspecified,
    val backgroundDataTypeRangingUpHeader: Color = Color.Unspecified,
    val backgroundDataTypeDataUpContainer: Color = Color.Unspecified,
    val backgroundDataTypeCarrierDownContainer: Color = Color.Unspecified,
    val backgroundDataTypeCarrierUpContainer: Color = Color.Unspecified,
    val backgroundDataTypeDataDownContainer: Color = Color.Unspecified,
    val backgroundDataTypeRangingUpContainer: Color = Color.Unspecified,
    val backgroundNoSignalHeader: Color = Color.Unspecified,
    val backgroundNoSignalContainer: Color = Color.Unspecified,
    val inactiveDishText: Color = Color.Unspecified,
    val noSpacecraftFound: Color = Color.Unspecified,
)

val LightCustomColorPalette = CustomColorPalette(
    infoWindowBackground = Color(0xFF29B6F6),
    batteryGreen = Color(0xFF00FF00),
    batteryYellow = Color(0xFFFFFF00),
    batteryRed = Color(0xFFFF0000),
    backgroundDataTypeCarrierUpHeader = Color(0xFFFFFBD2),
    backgroundDataTypeCarrierDownHeader = Color(0xFFDCF6DC),
    backgroundDataTypeDataUpHeader = Color(0xFFFFD7BA),
    backgroundDataTypeDataDownHeader = Color(0xFFDAEAF5),
    backgroundDataTypeRangingUpHeader = Color(0xFFFFD6DA),
    backgroundDataTypeDataUpContainer = Color(0xFFFFF6F0),
    backgroundDataTypeCarrierDownContainer = Color(0xFFF7FDF7),
    backgroundDataTypeCarrierUpContainer = Color(0xFFFFFEF5),
    backgroundDataTypeDataDownContainer = Color(0xFFF7FAFD),
    backgroundDataTypeRangingUpContainer = Color(0xFFFFF6F7),
    backgroundNoSignalHeader = Color(0xff745757),
    backgroundNoSignalContainer = Color(0xff8a5a5a),
    inactiveDishText = Color(0xff4f5153),
    noSpacecraftFound = Color(0xff4f5153),
)

val DarkCustomColorPalette = CustomColorPalette(
    infoWindowBackground = Color(0xAA000000),
    batteryGreen = Color(0xFF00FF00),
    batteryYellow = Color(0xFFFFFF00),
    batteryRed = Color(0xFFFF0000),
    backgroundDataTypeCarrierUpHeader = Color(0xFFFFFBD2),
    backgroundDataTypeCarrierDownHeader = Color(0xFFDCF6DC),
    backgroundDataTypeDataUpHeader = Color(0xff7b6858),
    backgroundDataTypeDataDownHeader = Color(0xff646f7a),
    backgroundDataTypeRangingUpHeader = Color(0xFFFFD6DA),
    backgroundDataTypeDataUpContainer = Color(0xff927659),
    backgroundDataTypeCarrierDownContainer = Color(0xFFF7FDF7),
    backgroundDataTypeCarrierUpContainer = Color(0xFFFFFEF5),
    backgroundDataTypeDataDownContainer = Color(0xff667a90),
    backgroundDataTypeRangingUpContainer = Color(0xFFFFF6F7),
    backgroundNoSignalHeader = Color(0xff745757),
    backgroundNoSignalContainer = Color(0xff8a5a5a),
    inactiveDishText = Color(0xff4f5153),
    noSpacecraftFound = Color(0xffbbbbbb),
)

val LocalCustomColorPalette = staticCompositionLocalOf { CustomColorPalette() }