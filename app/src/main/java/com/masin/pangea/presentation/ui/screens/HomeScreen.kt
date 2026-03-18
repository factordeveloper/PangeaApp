package com.masin.pangea.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masin.pangea.R
import com.masin.pangea.ui.theme.PANGEAappTheme

/** Breakpoints para diseño adaptativo (dp) */
private const val COMPACT_MAX = 600
private const val MEDIUM_MAX = 840
private const val CONTENT_MAX_WIDTH = 960

/**
 * Dimensiones escaladas según el ancho de pantalla para aprovechar el espacio
 * en tablets y evitar elementos pequeños con mucho espacio en blanco.
 */
private data class ResponsiveDimens(
    val screenWidth: Float,
    val screenHeight: Float,
    val isTablet: Boolean,
    val contentPadding: Dp,
    val sectionHorizontalPadding: Dp,
    val heroHeight: Dp,
    val titleFontSize: Float,
    val bodyFontSize: Float,
    val bodyLineHeight: Float,
    val circleSize: Dp,
    val circleFontSize: Float,
    val cardCornerRadius: Dp,
    val cardPadding: Dp,
    val sectionSpacing: Dp,
    val sectionVerticalPadding: Dp,
    val bottomSpacing: Dp
)

// Colores según el diseño de referencia (blanco, teal oscuro, fondo oscuro)
private val TealDark = Color(0xFF0D5C5C)
private val TealDarkAlt = Color(0xFF0A4A4A)
private val IntroBackgroundDark = Color(0xFF1A3A3A)
private val IntroBackgroundDarker = Color(0xFF0F2525)
private val TitleColor = Color(0xFF1A2E2E)
private val ScreenBackground = Color.White

/**
 * Calcula dimensiones responsivas según el ancho de pantalla.
 * En tablets: elementos más grandes y mejor aprovechamiento del espacio.
 */
@Composable
private fun rememberResponsiveDimens(): ResponsiveDimens {
    val config = LocalConfiguration.current
    val widthDp = config.screenWidthDp.toFloat()
    val heightDp = config.screenHeightDp.toFloat()
    val isTablet = widthDp >= COMPACT_MAX
    return remember(widthDp, heightDp) {
        val scale = when {
            widthDp < COMPACT_MAX -> 1f
            widthDp < MEDIUM_MAX -> 1.25f
            else -> 1.5f
        }
        val contentPad = when {
            widthDp < COMPACT_MAX -> 16.dp
            widthDp < MEDIUM_MAX -> 24.dp
            else -> 32.dp
        }
        ResponsiveDimens(
            screenWidth = widthDp,
            screenHeight = heightDp,
            isTablet = isTablet,
            contentPadding = contentPad,
            sectionHorizontalPadding = if (isTablet) {
                (widthDp * 0.06f).dp.coerceIn(32.dp, 56.dp)
            } else {
                contentPad
            },
            heroHeight = if (isTablet) {
                (heightDp * 0.18f).dp.coerceIn(140.dp, 200.dp)
            } else {
                (170 * scale).dp.coerceAtLeast(170.dp)
            },
            titleFontSize = 22f * scale,
            bodyFontSize = 11f * scale,
            bodyLineHeight = 15f * scale,
            circleSize = if (isTablet) {
                (heightDp * 0.15f).dp.coerceIn(120.dp, 160.dp)
            } else {
                (90 * scale).dp.coerceAtLeast(90.dp)
            },
            circleFontSize = if (isTablet) 15f else (12f * scale),
            cardCornerRadius = (12 * scale).dp,
            cardPadding = if (isTablet) 12.dp else (16 * scale).dp,
            sectionSpacing = if (isTablet) 16.dp else (12 * scale).dp,
            sectionVerticalPadding = if (isTablet) {
                (heightDp * 0.03f).dp.coerceIn(20.dp, 36.dp)
            } else {
                12.dp
            },
            bottomSpacing = if (isTablet) 24.dp else (80 * scale).dp
        )
    }
}

/**
 * Pantalla de inicio rediseñada con:
 * - Sección intro de Pangea (fondo oscuro)
 * - Sección "Sobre nosotros" con bloques informativos
 * - Sección "Descubre" con círculos de navegación
 * - Layout adaptativo para móviles y tablets
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun HomeScreen(
    onNavigateToPangea: () -> Unit,
    onNavigateToELearning: () -> Unit,
    onNavigateToDesk: () -> Unit,
    onNavigateToDigiturno: () -> Unit
) {
    val scrollState = rememberScrollState()
    val dimens = rememberResponsiveDimens()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        val contentMaxWidth = if (dimens.isTablet) {
            (maxWidth.value * 0.92f).dp
        } else {
            minOf(maxWidth, CONTENT_MAX_WIDTH.dp)
        }
        val horizontalPadding = (maxWidth - contentMaxWidth) / 2

        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (dimens.isTablet) Modifier else Modifier.verticalScroll(scrollState)
                )
        ) {
            // 1. Sección superior - Introducción a Pangea (fondo con imagen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.heroHeight)
                    .clickable(onClick = onNavigateToPangea)
            ) {
                Image(
                    painter = painterResource(R.drawable.fondo_texto),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.55f))
                )
                Text(
                    text = "Pangea nació del sueño de unir territorios mediante una ciudadanía digital que conecta personas, empresarios y comunidades bajo los valores de inclusión, sostenibilidad e innovación. Es un ecosistema donde cada espacio tiene un propósito: impulsar proyectos, formar talentos y demostrar que la tecnología puede transformar vidas.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimens.contentPadding)
                        .align(Alignment.Center),
                    fontSize = dimens.bodyFontSize.sp,
                    color = Color.White,
                    lineHeight = dimens.bodyLineHeight.sp
                )
            }

            Spacer(modifier = Modifier.height(if (dimens.isTablet) dimens.sectionVerticalPadding else dimens.sectionSpacing))

            // 2. Sección "Sobre nosotros"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (dimens.isTablet) Modifier.weight(1f) else Modifier)
                    .padding(horizontal = if (dimens.isTablet) dimens.sectionHorizontalPadding else horizontalPadding)
            ) {
                Text(
                    text = "Sobre nosotros",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = dimens.titleFontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = TitleColor
                )

                Spacer(modifier = Modifier.height(dimens.sectionSpacing))

                if (dimens.isTablet) {
                    // En tablet: 3 tarjetas en fila horizontal, altura ajustada al contenido
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(dimens.sectionSpacing)
                    ) {
                        TealInfoBlock(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            dimens = dimens,
                            text = "En este espacio encontrarás toda la información que necesitas para cumplir con tus obligaciones tributarias. Infórmate sobre los impuestos distritales, plazos, beneficios y normatividad vigente."
                        )
                        TealInfoBlock(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            dimens = dimens,
                            text = "Buscamos conectar emocionalmente con nuestros grupos de interés y compartir nuestra historia."
                        )
                        TealInfoBlock(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            dimens = dimens,
                            text = "Somos la Corporación de Territorios Inteligentes y Sostenibles (CTIS); Organización No Gubernamental colombiana."
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                } else {
                    Column(
                        modifier = Modifier
                            .width(contentMaxWidth)
                            .align(Alignment.CenterHorizontally)
                            .padding(horizontal = dimens.contentPadding),
                        verticalArrangement = Arrangement.spacedBy(dimens.sectionSpacing)
                    ) {
                        TealInfoBlock(
                            dimens = dimens,
                            text = "En este espacio encontrarás toda la información que necesitas para cumplir con tus obligaciones tributarias. Infórmate sobre los impuestos distritales, plazos, beneficios y normatividad vigente.",
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(dimens.sectionSpacing)
                        ) {
                            TealInfoBlock(
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                dimens = dimens,
                                text = "Buscamos conectar emocionalmente con nuestros grupos de interés y compartir nuestra historia."
                            )
                            TealInfoBlock(
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                dimens = dimens,
                                text = "Somos la Corporación de Territorios Inteligentes y Sostenibles (CTIS); Organización No Gubernamental colombiana."
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (dimens.isTablet) dimens.sectionVerticalPadding else dimens.sectionSpacing))

            // 3. Sección "Descubre"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (dimens.isTablet) dimens.sectionHorizontalPadding else horizontalPadding)
            ) {
                Text(
                    text = "Descubre",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = dimens.titleFontSize.sp,
                    fontWeight = FontWeight.Bold,
                    color = TitleColor
                )

                Spacer(modifier = Modifier.height(dimens.sectionSpacing))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (dimens.isTablet) 0.dp else (dimens.contentPadding.value * 1.5f).dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DiscoverCircle(
                        label = "DigiTurno",
                        dimens = dimens,
                        onClick = onNavigateToDigiturno
                    )
                    DiscoverCircle(
                        label = "E-Learning",
                        dimens = dimens,
                        onClick = onNavigateToELearning
                    )
                    DiscoverCircle(
                        label = "Desk",
                        dimens = dimens,
                        onClick = onNavigateToDesk
                    )
                }
            }

            if (!dimens.isTablet) {
                Spacer(modifier = Modifier.height(dimens.bottomSpacing))
            } else {
                Spacer(modifier = Modifier.height(dimens.sectionVerticalPadding))
            }
        }
    }
}

/**
 * Bloque informativo con fondo teal oscuro y texto blanco
 */
@Composable
private fun TealInfoBlock(
    modifier: Modifier = Modifier,
    dimens: ResponsiveDimens,
    text: String
) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(dimens.cardCornerRadius))
            .clip(RoundedCornerShape(dimens.cardCornerRadius)),
        shape = RoundedCornerShape(dimens.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = TealDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(dimens.cardPadding),
            fontSize = dimens.bodyFontSize.sp,
            color = Color.White,
            lineHeight = dimens.bodyLineHeight.sp
        )
    }
}

/**
 * Círculo de navegación para la sección "Descubre"
 */
@Composable
private fun DiscoverCircle(
    label: String,
    dimens: ResponsiveDimens,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(dimens.circleSize)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = TealDarkAlt),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(if (dimens.isTablet) 4.dp else (dimens.cardPadding.value / 2).dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = dimens.circleFontSize.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PANGEAappTheme {
        HomeScreen(
            onNavigateToPangea = {},
            onNavigateToELearning = {},
            onNavigateToDesk = {},
            onNavigateToDigiturno = {}
        )
    }
}
