package com.masin.pangea.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
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
import com.masin.pangea.ui.theme.PangeaCyan
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
    onNavigateToDigiturno: () -> Unit,
    onNavigateToRadar: () -> Unit
) {
    val scrollState = rememberScrollState()
    val dimens = rememberAppDimens()
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefs = androidx.compose.runtime.remember { context.getSharedPreferences("pangea_prefs", android.content.Context.MODE_PRIVATE) }
    val selectedPlanStr = sharedPrefs.getString("selected_plan", "BASIC")
    val isPremium = selectedPlanStr == "PREMIUM" || selectedPlanStr == "ENTERPRISE"

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
                    text = "Pangea, un ecosistema que impulsa proyectos, forma talentos y transforma vidas mediante la tecnología.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.paddingScreen)
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

                TealInfoBlock(
                    modifier = Modifier.fillMaxWidth(),
                    dimens = dimens,
                    text = "CTIS, la Corporación de Territorios Inteligentes y Sostenibles del Grupo Masin nació hace más de 30 años en Colombia, con el propósito de transformar territorios y comunidades."
                )
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

                Spacer(modifier = Modifier.height(dimens.spacingMedium))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DiscoverCircle(
                        label = "Agente IA",
                        dimens = dimens,
                        backgroundRes = R.drawable.agente,
                        isLocked = !isPremium,
                        onClick = {}
                    )
                    DiscoverCircle(
                        label = "Event. en vivo",
                        dimens = dimens,
                        backgroundRes = R.drawable.live,
                        isLocked = !isPremium,
                        onClick = {}
                    )
                    DiscoverCircle(
                        label = "Admin. Pass",
                        dimens = dimens,
                        backgroundRes = R.drawable.gestor_pass,
                        isLocked = !isPremium,
                        onClick = {}
                    )
                }
                Spacer(modifier = Modifier.height(dimens.spacingMedium))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DiscoverCircle(
                        label = "Radar territ.",
                        dimens = dimens,
                        backgroundRes = R.drawable.radar_territorial,
                        isLocked = !isPremium,
                        onClick = { if (isPremium) onNavigateToRadar() }
                    )
                    DiscoverCircle(
                        label = "Pago en línea",
                        dimens = dimens,
                        backgroundRes = R.drawable.pagos,
                        isLocked = !isPremium,
                        onClick = {}
                    )
                    DiscoverCircle(
                        label = "Agendamiento",
                        dimens = dimens,
                        backgroundRes = R.drawable.calendario,
                        isLocked = !isPremium,
                        onClick = {}
                    )
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
    isLocked: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(dimens.circleItemSize)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .then(
                if (isLocked) {
                    Modifier.border(1.dp, PangeaCyan.copy(alpha = 0.5f), CircleShape)
                } else {
                    Modifier.border(1.5.dp, PangeaCyan, CircleShape)
                }
            )
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
                    .background(Color.Black.copy(alpha = if (isLocked) 0.65f else 0.35f))
            )
            if (isLocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
            Text(
                text = label,
                fontSize = dimens.fontCaption,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = if (isLocked) 0.7f else 1f),
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
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
            onNavigateToDigiturno = {},
            onNavigateToRadar = {}
        )
    }
}
