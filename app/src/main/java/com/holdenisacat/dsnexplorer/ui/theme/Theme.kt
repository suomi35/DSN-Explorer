package com.holdenisacat.dsnexplorer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    secondary = Red80,
    tertiary = Grey80,
)

private val LightColorScheme = lightColorScheme(
    primary = NasaBlue,
    secondary = NasaRed,
    tertiary = Grey40,
)

@Composable
fun DSNExplorerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val customColorPalette = if (darkTheme) DarkCustomColorPalette else LightCustomColorPalette

    CompositionLocalProvider(LocalCustomColorPalette provides customColorPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
