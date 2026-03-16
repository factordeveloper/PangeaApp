package com.masin.pangea.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masin.pangea.ui.theme.PANGEAappTheme

// Colores según el diseño de la imagen
private val CreamBackground = Color(0xFFF5F0E8)
private val CardBorderRed = Color(0xFF8B0000)
private val DiscoverBlockGreen = Color(0xFF41E8B0)

/**
 * Pantalla informativa de Pangea con secciones:
 * - Introducción a Pangea
 * - Sobre nosotros (3 tarjetas)
 * - Descubre (3 bloques interactivos: E-learning, Desk, Digiturno)
 */
@Composable
fun PangeaScreen(
    onNavigateToElearning: () -> Unit = {},
    onNavigateToDesk: () -> Unit = {},
    onNavigateToDigiturno: () -> Unit = {}
) {
    val scrollState = rememberScrollState()
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val useColumnLayout = screenWidthDp < 600

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Sección 1: Introducción a Pangea
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Text(
                text = "Pangea nació del sueño de unir territorios mediante una ciudadanía digital que conecta personas, empresarios y comunidades bajo los valores de inclusión, sostenibilidad e innovación. Es un ecosistema donde cada espacio tiene un propósito: impulsar proyectos, formar talentos y demostrar que la tecnología puede transformar vidas.",
                modifier = Modifier.padding(20.dp),
                fontSize = 15.sp,
                color = Color.Black,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección 2: Sobre nosotros
        Text(
            text = "Sobre nosotros",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (useColumnLayout) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                AboutCard(
                    modifier = Modifier.fillMaxWidth(),
                    text = "La Corporación CTIS del Grupo Masin nació hace más de 30 años en Colombia, con el propósito de transformar territorios y comunidades."
                )
                AboutCard(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Buscamos conectar emocionalmente con nuestros grupos de interés y compartir nuestra historia."
                )
                AboutCard(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Somos la Corporación de Territorios Inteligentes y Sostenibles (CTIS); Organización No Gubernamental colombiana."
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AboutCard(
                    modifier = Modifier.weight(1f),
                    text = "La Corporación CTIS del Grupo Masin nació hace más de 30 años en Colombia, con el propósito de transformar territorios y comunidades."
                )
                AboutCard(
                    modifier = Modifier.weight(1f),
                    text = "Buscamos conectar emocionalmente con nuestros grupos de interés y compartir nuestra historia."
                )
                AboutCard(
                    modifier = Modifier.weight(1f),
                    text = "Somos la Corporación de Territorios Inteligentes y Sostenibles (CTIS); Organización No Gubernamental colombiana."
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sección 3: Descubre
        Text(
            text = "Descubre",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (useColumnLayout) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DiscoverBlock(
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    text = "E - learning",
                    onClick = onNavigateToElearning
                )
                DiscoverBlock(
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    text = "Desk",
                    onClick = onNavigateToDesk
                )
                DiscoverBlock(
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    text = "Digiturno",
                    onClick = onNavigateToDigiturno
                )
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DiscoverBlock(
                    modifier = Modifier.weight(1f).height(100.dp),
                    text = "E - learning",
                    onClick = onNavigateToElearning
                )
                DiscoverBlock(
                    modifier = Modifier.weight(1f).height(100.dp),
                    text = "Digiturno",
                    onClick = onNavigateToDigiturno
                )
                DiscoverBlock(
                    modifier = Modifier.weight(1f).height(100.dp),
                    text = "Desk",
                    onClick = onNavigateToDesk
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun AboutCard(
    modifier: Modifier = Modifier,
    text: String
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CardBorderRed, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            fontSize = 13.sp,
            color = Color.Black,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun DiscoverBlock(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, CardBorderRed, RoundedCornerShape(12.dp))
            .background(DiscoverBlockGreen)
            .clickable(onClick = onClick)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PangeaScreenPreview() {
    PANGEAappTheme {
        PangeaScreen()
    }
}
