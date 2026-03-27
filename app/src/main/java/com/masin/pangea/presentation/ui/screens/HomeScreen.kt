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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.masin.pangea.R
import com.masin.pangea.presentation.ui.utils.AppDimens
import com.masin.pangea.presentation.ui.utils.WindowSizeClass
import com.masin.pangea.presentation.ui.utils.rememberAppDimens
import com.masin.pangea.ui.theme.PANGEAappTheme
import coil.compose.AsyncImage
import coil.request.ImageRequest

// Colores del módulo Home
private val TealDark = Color(0xFF0D5C5C)
private val TitleColor = Color(0xFF1A2E2E)
private val ScreenBackground = Color.White

/**
 * Pantalla de inicio con diseño responsivo usando el sistema centralizado AppDimens.
 * - Sección intro de Pangea (fondo con imagen)
 * - Sección "Sobre nosotros" con tarjetas informativas
 * - Sección "Descubre" con círculos de navegación
 * - Scroll siempre activo (incluido tablet) para evitar contenido cortado
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
    val dimens = rememberAppDimens()
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = androidx.compose.runtime.remember { context.getSharedPreferences("pangea_prefs", android.content.Context.MODE_PRIVATE) }
    val selectedPlanStr = androidx.compose.runtime.remember { sharedPrefs.getString("selected_plan", "BASIC") }
    val isPremium = selectedPlanStr == "PREMIUM"

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        val contentMaxWidth = dimens.maxContentWidth
        val horizontalPad = ((maxWidth - contentMaxWidth) / 2).coerceAtLeast(0.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)          // Scroll siempre activo — evita contenido cortado en tablet
        ) {
            // ── 1. Sección Hero / Intro ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimens.heroImageSize)
                    .clickable(onClick = onNavigateToPangea)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(R.drawable.fondo_texto)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
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
                        .padding(dimens.paddingScreen)
                        .align(Alignment.Center),
                    fontSize = dimens.fontBody,
                    color = Color.White,
                    lineHeight = dimens.lineHeightBody
                )
            }

            Spacer(modifier = Modifier.height(dimens.spacingLarge))

            // ── 2. Sección "Sobre nosotros" ──────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.paddingSection + horizontalPad)
            ) {
                Text(
                    text = "Sobre nosotros",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = dimens.fontTitle,
                    fontWeight = FontWeight.Bold,
                    color = TitleColor
                )

                Spacer(modifier = Modifier.height(dimens.spacingMedium))

                if (dimens.isTablet) {
                    // Tablet: 3 tarjetas en fila horizontal
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
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
                } else {
                    // Móvil: 1 tarjeta arriba + 2 tarjetas en fila abajo
                    Column(
                        verticalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
                    ) {
                        TealInfoBlock(
                            dimens = dimens,
                            text = "En este espacio encontrarás toda la información que necesitas para cumplir con tus obligaciones tributarias. Infórmate sobre los impuestos distritales, plazos, beneficios y normatividad vigente."
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
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

            Spacer(modifier = Modifier.height(dimens.spacingLarge))

            // ── 3. Sección "Descubre" ─────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.paddingSection + horizontalPad)
            ) {
                Text(
                    text = "Descubre",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = dimens.fontTitle,
                    fontWeight = FontWeight.Bold,
                    color = TitleColor
                )

                Spacer(modifier = Modifier.height(dimens.spacingMedium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DiscoverCircle(
                        label = "DigiTurno",
                        dimens = dimens,
                        backgroundRes = R.drawable.digiturno,
                        onClick = onNavigateToDigiturno
                    )
                    DiscoverCircle(
                        label = "E-Learning",
                        dimens = dimens,
                        backgroundRes = R.drawable.elearning,
                        onClick = onNavigateToELearning
                    )
                    DiscoverCircle(
                        label = "Desk",
                        dimens = dimens,
                        backgroundRes = R.drawable.desk,
                        onClick = onNavigateToDesk
                    )
                }

                if (isPremium) {
                    Spacer(modifier = Modifier.height(dimens.spacingMedium))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DiscoverCircle(label = "Agente IA", dimens = dimens, backgroundRes = R.drawable.agente, onClick = {})
                        DiscoverCircle(label = "Event. en vivo", dimens = dimens, backgroundRes = R.drawable.live, onClick = {})
                        DiscoverCircle(label = "Admin. Pass", dimens = dimens, backgroundRes = R.drawable.gestor_pass, onClick = {})
                    }
                    Spacer(modifier = Modifier.height(dimens.spacingMedium))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        DiscoverCircle(label = "Radar territ.", dimens = dimens, backgroundRes = R.drawable.radar_territorial, onClick = {})
                        DiscoverCircle(label = "Pago en línea", dimens = dimens, backgroundRes = R.drawable.pagos, onClick = {})
                        DiscoverCircle(label = "Agendamiento", dimens = dimens, backgroundRes = R.drawable.calendario, onClick = {})
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimens.bottomBarSpacing))
        }
    }
}

/**
 * Bloque informativo con fondo teal oscuro y texto blanco
 */
@Composable
private fun TealInfoBlock(
    modifier: Modifier = Modifier,
    dimens: AppDimens,
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
            modifier = Modifier.padding(dimens.paddingCard),
            fontSize = dimens.fontBody,
            color = Color.White,
            lineHeight = dimens.lineHeightBody
        )
    }
}

/**
 * Círculo de navegación para la sección "Descubre"
 */
@Composable
private fun DiscoverCircle(
    label: String,
    dimens: AppDimens,
    backgroundRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(dimens.circleItemSize)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        shape = CircleShape,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val context = androidx.compose.ui.platform.LocalContext.current
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(backgroundRes)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.35f))
            )
            Text(
                text = label,
                fontSize = dimens.fontCaption,
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
