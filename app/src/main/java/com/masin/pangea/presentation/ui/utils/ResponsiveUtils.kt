package com.masin.pangea.presentation.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Breakpoints para diseño adaptativo (dp).
 * - COMPACT: 0..599 (teléfonos en portrait)
 * - MEDIUM:  600..839 (tablets pequeñas / teléfonos en landscape)
 * - EXPANDED: 840+    (tablets grandes)
 */
object ScreenBreakpoint {
    const val COMPACT_MAX = 600
    const val MEDIUM_MAX = 840
}

/**
 * Tipo de tamaño de ventana según los breakpoints de Material Design 3.
 */
enum class WindowSizeClass {
    COMPACT, MEDIUM, EXPANDED
}

/**
 * Dimensiones responsivas centralizadas que escalan según el tamaño de pantalla.
 *
 * Cada pantalla puede usar estos valores base y opcionalmente añadir
 * dimensiones específicas propias.
 */
data class AppDimens(
    val windowSizeClass: WindowSizeClass,
    val screenWidthDp: Float,
    val screenHeightDp: Float,
    val isTablet: Boolean,

    // ── Escala general ──
    val scaleFactor: Float,

    // ── Fuentes ──
    /** Título principal grande (28sp base) */
    val fontHero: TextUnit,
    /** Título de sección (22-24sp base) */
    val fontTitle: TextUnit,
    /** Subtítulo / Header de card (18sp base) */
    val fontSubtitle: TextUnit,
    /** Cuerpo de texto principal (14-16sp base) */
    val fontBody: TextUnit,
    /** Texto pequeño / etiquetas (12sp base) */
    val fontCaption: TextUnit,
    /** Texto diminuto / badges (10-11sp base) */
    val fontSmall: TextUnit,

    // ── Line heights ──
    val lineHeightBody: TextUnit,
    val lineHeightSmall: TextUnit,

    // ── Spacing ──
    val paddingScreen: Dp,
    val paddingCard: Dp,
    val paddingSection: Dp,
    val spacingSmall: Dp,
    val spacingMedium: Dp,
    val spacingLarge: Dp,
    val spacingXLarge: Dp,

    // ── Componentes ──
    val cardCornerRadius: Dp,
    val buttonHeight: Dp,
    val buttonCornerRadius: Dp,
    val iconSizeSmall: Dp,
    val iconSizeMedium: Dp,
    val iconSizeLarge: Dp,
    val avatarSize: Dp,
    val thumbnailSize: Dp,

    // ── Imágenes / Logos ──
    val logoSize: Dp,
    val heroImageSize: Dp,
    val circleItemSize: Dp,

    // ── Bottom bar compensation ──
    val bottomBarSpacing: Dp,

    // ── Max content width para centrar en tablets anchas ──
    val maxContentWidth: Dp
)

/**
 * Calcula y recuerda las dimensiones responsivas según el ancho y alto de pantalla.
 */
@Composable
fun rememberAppDimens(): AppDimens {
    val config = LocalConfiguration.current
    val widthDp = config.screenWidthDp.toFloat()
    val heightDp = config.screenHeightDp.toFloat()

    return remember(widthDp, heightDp) {
        val windowSizeClass = when {
            widthDp < ScreenBreakpoint.COMPACT_MAX -> WindowSizeClass.COMPACT
            widthDp < ScreenBreakpoint.MEDIUM_MAX -> WindowSizeClass.MEDIUM
            else -> WindowSizeClass.EXPANDED
        }

        val isTablet = windowSizeClass != WindowSizeClass.COMPACT

        // Factor de escala progresivo basado en el ancho
        val scale = when (windowSizeClass) {
            WindowSizeClass.COMPACT -> {
                // Escalar proporcionalmente dentro del rango compacto (320..599)
                ((widthDp - 320f) / (600f - 320f)).coerceIn(0f, 1f) * 0.15f + 0.92f
            }
            WindowSizeClass.MEDIUM -> {
                ((widthDp - 600f) / (840f - 600f)).coerceIn(0f, 1f) * 0.25f + 1.10f
            }
            WindowSizeClass.EXPANDED -> {
                ((widthDp - 840f) / (1200f - 840f)).coerceIn(0f, 1f) * 0.20f + 1.35f
            }
        }

        // Factor de escala para fuentes: un poco más conservador en tablets
        val fontScale = when (windowSizeClass) {
            WindowSizeClass.COMPACT -> scale
            WindowSizeClass.MEDIUM -> 1.0f + (scale - 1.0f) * 0.8f
            WindowSizeClass.EXPANDED -> 1.0f + (scale - 1.0f) * 0.7f
        }

        AppDimens(
            windowSizeClass = windowSizeClass,
            screenWidthDp = widthDp,
            screenHeightDp = heightDp,
            isTablet = isTablet,
            scaleFactor = scale,

            // ── Fuentes escaladas ──
            fontHero = (28f * fontScale).sp,
            fontTitle = (22f * fontScale).sp,
            fontSubtitle = (18f * fontScale).sp,
            fontBody = (14f * fontScale).sp,
            fontCaption = (12f * fontScale).sp,
            fontSmall = (11f * fontScale).sp,

            // ── Line heights ──
            lineHeightBody = (20f * fontScale).sp,
            lineHeightSmall = (15f * fontScale).sp,

            // ── Spacing ──
            paddingScreen = when (windowSizeClass) {
                WindowSizeClass.COMPACT -> 16.dp
                WindowSizeClass.MEDIUM -> 24.dp
                WindowSizeClass.EXPANDED -> 32.dp
            },
            paddingCard = (16f * scale.coerceIn(0.9f, 1.3f)).dp,
            paddingSection = when (windowSizeClass) {
                WindowSizeClass.COMPACT -> 20.dp
                WindowSizeClass.MEDIUM -> 28.dp
                WindowSizeClass.EXPANDED -> 36.dp
            },
            spacingSmall = (8f * scale.coerceIn(0.9f, 1.2f)).dp,
            spacingMedium = (12f * scale.coerceIn(0.9f, 1.3f)).dp,
            spacingLarge = (24f * scale.coerceIn(0.9f, 1.3f)).dp,
            spacingXLarge = (40f * scale.coerceIn(0.9f, 1.3f)).dp,

            // ── Componentes ──
            cardCornerRadius = (12f * scale.coerceIn(1f, 1.4f)).dp,
            buttonHeight = when (windowSizeClass) {
                WindowSizeClass.COMPACT -> (48f * scale).dp.coerceIn(44.dp, 56.dp)
                WindowSizeClass.MEDIUM -> 56.dp
                WindowSizeClass.EXPANDED -> 60.dp
            },
            buttonCornerRadius = (24f * scale.coerceIn(1f, 1.3f)).dp,
            iconSizeSmall = (18f * scale.coerceIn(0.9f, 1.3f)).dp,
            iconSizeMedium = (24f * scale.coerceIn(0.9f, 1.3f)).dp,
            iconSizeLarge = (36f * scale.coerceIn(0.9f, 1.4f)).dp,
            avatarSize = when (windowSizeClass) {
                WindowSizeClass.COMPACT -> (40f * scale).dp.coerceIn(36.dp, 48.dp)
                WindowSizeClass.MEDIUM -> 48.dp
                WindowSizeClass.EXPANDED -> 56.dp
            },
            thumbnailSize = when (windowSizeClass) {
                WindowSizeClass.COMPACT -> (72f * scale).dp.coerceIn(64.dp, 84.dp)
                WindowSizeClass.MEDIUM -> 88.dp
                WindowSizeClass.EXPANDED -> 100.dp
            },

            // ── Imágenes / Logos ──
            logoSize = when (windowSizeClass) {
                WindowSizeClass.COMPACT -> (100f * scale).dp.coerceIn(80.dp, 120.dp)
                WindowSizeClass.MEDIUM -> 140.dp
                WindowSizeClass.EXPANDED -> 160.dp
            },
            heroImageSize = when (windowSizeClass) {
                WindowSizeClass.COMPACT -> (240f * scale).dp.coerceIn(200.dp, 280.dp)
                WindowSizeClass.MEDIUM -> 300.dp
                WindowSizeClass.EXPANDED -> 340.dp
            },
            circleItemSize = when (windowSizeClass) {
                WindowSizeClass.COMPACT -> (90f * scale).dp.coerceIn(80.dp, 110.dp)
                WindowSizeClass.MEDIUM -> 120.dp
                WindowSizeClass.EXPANDED -> 140.dp
            },

            // ── Bottom bar ──
            bottomBarSpacing = when (windowSizeClass) {
                WindowSizeClass.COMPACT -> 80.dp
                else -> 24.dp
            },

            // ── Max content width ──
            maxContentWidth = when (windowSizeClass) {
                WindowSizeClass.COMPACT -> widthDp.dp
                WindowSizeClass.MEDIUM -> (widthDp * 0.92f).dp.coerceAtMost(720.dp)
                WindowSizeClass.EXPANDED -> (widthDp * 0.85f).dp.coerceAtMost(960.dp)
            }
        )
    }
}

/**
 * Extensión para escalar un Dp según el factor de escala,
 * con límites mínimo y máximo opcionales.
 */
fun Dp.scaled(factor: Float, min: Dp = Dp.Unspecified, max: Dp = Dp.Unspecified): Dp {
    val result = (this.value * factor).dp
    val withMin = if (min != Dp.Unspecified) result.coerceAtLeast(min) else result
    return if (max != Dp.Unspecified) withMin.coerceAtMost(max) else withMin
}

/**
 * Extensión para escalar un TextUnit (sp) según el factor de escala.
 */
fun TextUnit.scaled(factor: Float): TextUnit {
    return (this.value * factor).sp
}
