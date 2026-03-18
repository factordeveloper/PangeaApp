package com.masin.pangea.presentation.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masin.pangea.R
import com.masin.pangea.ui.theme.PANGEAappTheme

// Colores del módulo Desk (fondo teal oscuro con gradiente)
private val DeskBackgroundDark = Color(0xFF0F2525)
private val DeskBackgroundTeal = Color(0xFF1A3A3A)
private val CardTeal = Color(0xFF0D5C5C)
private val CardTealAlt = Color(0xFF0A4A4A)
private val ButtonDark = Color(0xFF1A2E2E)
private val TextWhite = Color.White

/**
 * Pantalla del módulo Desk con UI nativa.
 * Incluye: header, sección de login/registro, tarjetas de acción (Mis Casos, Crear Caso)
 * y sección de recomendaciones.
 */
@Composable
fun DeskScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeskBackgroundTeal, DeskBackgroundDark)
                )
            )
            .verticalScroll(scrollState)
    ) {
        // 1. Header: título y descripción
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "DESK",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "En este espacio encontrarás toda la información que necesitas para cumplir con tus obligaciones tributarias. Infórmate sobre los impuestos distritales, plazos, beneficios y normatividad vigente.",
                fontSize = 14.sp,
                color = TextWhite,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        // 2. Fila Login / Branding (grid 2 columnas)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Tarjeta Login / Registro
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardTeal),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = { /* TODO: navegar a login */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonDark)
                    ) {
                        Text("Iniciar sesión", color = TextWhite)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "¿No tienes una cuenta?",
                        fontSize = 12.sp,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { /* TODO: navegar a crear cuenta */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonDark)
                    ) {
                        Text("Crear cuenta", color = TextWhite)
                    }
                }
            }

            // Tarjeta Branding Pangea (imagen + logo)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(180.dp)
                    .clip(RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardTeal),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(R.drawable.fondo_texto),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(R.drawable.pangea),
                            contentDescription = "Pangea",
                            modifier = Modifier.size(100.dp),
                            contentScale = ContentScale.Fit
                        )

                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Tarjetas de acción: MIS CASOS y CREAR CASO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DeskActionCard(
                title = "MIS CASOS",
                subtitle = "lorem impus lorem impus",
                onClick = { /* TODO */ }
            )
            DeskActionCard(
                title = "CREAR CASO",
                subtitle = "lorem impus lorem impus",
                onClick = { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Sección Recomendaciones
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Recomendaciones",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            DeskActionCard(
                title = "LOREM IMPUS",
                subtitle = "lorem impus lorem impus",
                onClick = { /* TODO */ }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun DeskActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardTealAlt),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = TextWhite.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeskScreenPreview() {
    PANGEAappTheme {
        DeskScreen()
    }
}
