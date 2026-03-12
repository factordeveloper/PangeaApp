package com.masin.pangea.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Color scheme ligero pre-definido para evitar cálculos en runtime.
 * Usar colores estáticos en lugar de dynamic colors mejora el cold start.
 */
private val PANGEAColorScheme = lightColorScheme(
    primary = Color(0xFFAC1927),          // SDH Red
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD6),
    onPrimaryContainer = Color(0xFF410002),
    secondary = Color(0xFF775652),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDAD6),
    onSecondaryContainer = Color(0xFF2C1512),
    tertiary = Color(0xFF755A2F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDEAD),
    onTertiaryContainer = Color(0xFF281900),
    background = Color.White,
    onBackground = Color(0xFF201A19),
    surface = Color.White,
    onSurface = Color(0xFF201A19),
    surfaceVariant = Color(0xFFF5DDDA),
    onSurfaceVariant = Color(0xFF534341),
    outline = Color(0xFF857370),
    outlineVariant = Color(0xFFD8C2BF)
)

/**
 * Tema optimizado para rendimiento:
 * - Sin dynamic colors (evita llamadas al sistema)
 * - Sin detección de dark theme (app siempre light)
 * - Color scheme pre-calculado
 */
@Composable
fun PANGEAappTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PANGEAColorScheme,
        typography = Typography,
        content = content
    )
}