package com.masin.pangea.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masin.pangea.ui.theme.PANGEAappTheme

// Colores según el diseño de referencia (blanco, teal oscuro, fondo oscuro)
private val TealDark = Color(0xFF0D5C5C)
private val TealDarkAlt = Color(0xFF0A4A4A)
private val IntroBackgroundDark = Color(0xFF1A3A3A)
private val IntroBackgroundDarker = Color(0xFF0F2525)
private val TitleColor = Color(0xFF1A2E2E)
private val ScreenBackground = Color.White

/**
 * Pantalla de inicio rediseñada con:
 * - Sección intro de Pangea (fondo oscuro)
 * - Sección "Sobre nosotros" con bloques informativos
 * - Sección "Descubre" con círculos de navegación
 */
@Composable
fun HomeScreen(
    onNavigateToPangea: () -> Unit,
    onNavigateToELearning: () -> Unit,
    onNavigateToDesk: () -> Unit,
    onNavigateToDigiturno: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .verticalScroll(scrollState)
    ) {
        // 1. Sección superior - Introducción a Pangea (fondo oscuro)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(IntroBackgroundDark, IntroBackgroundDarker)
                    )
                )
                .clickable(onClick = onNavigateToPangea)
                .padding(20.dp)
        ) {
            Text(
                text = "Pangea nació del sueño de unir territorios mediante una ciudadanía digital que conecta personas, empresarios y comunidades bajo los valores de inclusión, sostenibilidad e innovación. Es un ecosistema donde cada espacio tiene un propósito: impulsar proyectos, formar talentos y demostrar que la tecnología puede transformar vidas.",
                fontSize = 11.sp,
                color = Color.White,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Sección "Sobre nosotros"
        Text(
            text = "Sobre nosotros",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TitleColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Bloque principal grande
            TealInfoBlock(
                text = "En este espacio encontrarás toda la información que necesitas para cumplir con tus obligaciones tributarias. Infórmate sobre los impuestos distritales, plazos, beneficios y normatividad vigente.",
            )

            // Dos bloques secundarios lado a lado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TealInfoBlock(
                    modifier = Modifier.weight(1f),
                    text = "Buscamos conectar emocionalmente con nuestros grupos de interés y compartir nuestra historia."
                )
                TealInfoBlock(
                    modifier = Modifier.weight(1f),
                    text = "Somos la Corporación de Territorios Inteligentes y Sostenibles (CTIS); Organización No Gubernamental colombiana."
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 3. Sección "Descubre"
        Text(
            text = "Descubre",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = TitleColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DiscoverCircle(
                label = "DigiTurno",
                onClick = onNavigateToDigiturno
            )
            DiscoverCircle(
                label = "E-Learning",
                onClick = onNavigateToELearning
            )
            DiscoverCircle(
                label = "Desk",
                onClick = onNavigateToDesk
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * Bloque informativo con fondo teal oscuro y texto blanco
 */
@Composable
private fun TealInfoBlock(
    modifier: Modifier = Modifier,
    text: String
) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TealDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            fontSize = 11.sp,
            color = Color.White,
            lineHeight = 15.sp
        )
    }
}

/**
 * Círculo de navegación para la sección "Descubre"
 */
@Composable
private fun DiscoverCircle(
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(90.dp)
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
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
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
