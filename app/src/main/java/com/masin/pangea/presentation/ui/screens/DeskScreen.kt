package com.masin.pangea.presentation.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.masin.pangea.R
import com.masin.pangea.presentation.ui.utils.rememberAppDimens
import com.masin.pangea.ui.theme.PANGEAappTheme

// Colores del módulo Desk (fondo teal oscuro con gradiente)
private val DeskBackgroundDark = Color.White
private val DeskBackgroundTeal = Color(0xFF1A3A3A)
private val CardTeal = Color(0xFF0D5C5C)
private val CardTealAlt = Color(0xFF0A4A4A)
private val ButtonDark = Color(0xFF1A2E2E)
private val TextWhite = Color.White

/**
 * Pantalla del módulo Desk con UI nativa.
 * Incluye: header, sección de login/registro, tarjetas de acción (Mis Casos, Crear Caso)
 * y sección de recomendaciones.
 * Adaptativa para móviles y tablets.
 */
@Composable
fun DeskScreen() {
    val dimens = rememberAppDimens()
    val scrollState = rememberScrollState()
    var showAuthModal by remember { mutableStateOf(false) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val contentMaxWidth = dimens.maxContentWidth
        val horizontalPad = ((maxWidth - contentMaxWidth) / 2).coerceAtLeast(0.dp)

        Box(modifier = Modifier.fillMaxSize()) {
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
                        .padding(
                            horizontal = dimens.paddingSection + horizontalPad,
                            vertical = dimens.spacingLarge
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DESK",
                        fontSize = dimens.fontHero,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(dimens.spacingMedium))
                    Text(
                        text = "En este espacio encontrarás toda la información que necesitas para cumplir con tus obligaciones tributarias. Infórmate sobre los impuestos distritales, plazos, beneficios y normatividad vigente.",
                        fontSize = dimens.fontBody,
                        color = TextWhite,
                        lineHeight = dimens.lineHeightBody,
                        textAlign = TextAlign.Center
                    )
                }

                // 2. Fila Login / Branding (grid 2 columnas)
                val cardHeight = if (dimens.isTablet) {
                    (dimens.screenHeightDp * 0.25f).dp.coerceIn(180.dp, 260.dp)
                } else {
                    (180f * dimens.scaleFactor).dp.coerceIn(160.dp, 220.dp)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.paddingScreen + horizontalPad),
                    horizontalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
                ) {
                    // Tarjeta Login / Registro
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(cardHeight)
                            .clip(RoundedCornerShape(dimens.cardCornerRadius * 1.5f)),
                        shape = RoundedCornerShape(dimens.cardCornerRadius * 1.5f),
                        colors = CardDefaults.cardColors(containerColor = CardTeal),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(dimens.paddingCard),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Button(
                                onClick = { /* TODO: navegar a login */ },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(dimens.buttonCornerRadius),
                                colors = ButtonDefaults.buttonColors(containerColor = ButtonDark)
                            ) {
                                Text(
                                    "Iniciar sesión",
                                    color = TextWhite,
                                    fontSize = dimens.fontCaption
                                )
                            }
                            Spacer(modifier = Modifier.height(dimens.spacingMedium))
                            Text(
                                text = "¿No tienes una cuenta?",
                                fontSize = dimens.fontCaption,
                                color = TextWhite
                            )
                            Spacer(modifier = Modifier.height(dimens.spacingSmall))
                            Button(
                                onClick = { /* TODO: navegar a crear cuenta */ },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(dimens.buttonCornerRadius),
                                colors = ButtonDefaults.buttonColors(containerColor = ButtonDark)
                            ) {
                                Text(
                                    "Crear cuenta",
                                    color = TextWhite,
                                    fontSize = dimens.fontCaption
                                )
                            }
                        }
                    }

                    // Tarjeta Branding Pangea (imagen + logo)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(cardHeight)
                            .clip(RoundedCornerShape(dimens.cardCornerRadius * 1.5f)),
                        shape = RoundedCornerShape(dimens.cardCornerRadius * 1.5f),
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
                                    .padding(dimens.paddingCard),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.pangea),
                                    contentDescription = "Pangea",
                                    modifier = Modifier.size(dimens.logoSize),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimens.spacingLarge))

                // 3. Tarjetas de acción: MIS CASOS y CREAR CASO
                if (dimens.isTablet) {
                    // En tablet: 2 tarjetas en fila
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimens.paddingScreen + horizontalPad)
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            DeskActionCard(
                                title = "MIS CASOS",
                                subtitle = "lorem impus lorem impus",
                                onClick = { showAuthModal = true },
                                dimens = dimens
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DeskActionCard(
                                title = "CREAR CASO",
                                subtitle = "lorem impus lorem impus",
                                onClick = { showAuthModal = true },
                                dimens = dimens
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimens.paddingScreen),
                        verticalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
                    ) {
                        DeskActionCard(
                            title = "MIS CASOS",
                            subtitle = "lorem impus lorem impus",
                            onClick = { showAuthModal = true },
                            dimens = dimens
                        )
                        DeskActionCard(
                            title = "CREAR CASO",
                            subtitle = "lorem impus lorem impus",
                            onClick = { showAuthModal = true },
                            dimens = dimens
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimens.spacingLarge))

                // 4. Sección Recomendaciones
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.paddingScreen + horizontalPad)
                ) {
                    Text(
                        text = "Recomendaciones",
                        fontSize = dimens.fontSubtitle,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = dimens.spacingMedium)
                    )
                    DeskActionCard(
                        title = "LOREM IMPUS",
                        subtitle = "lorem impus lorem impus",
                        onClick = { /* TODO */ },
                        dimens = dimens
                    )
                }

                Spacer(modifier = Modifier.height(dimens.bottomBarSpacing))
            }

            if (showAuthModal) {
                DeskAuthModal(
                    onDismiss = { showAuthModal = false },
                    onLoginClick = { /* TODO: navegar a login */ showAuthModal = false },
                    onCreateAccountClick = { /* TODO: navegar a crear cuenta */ showAuthModal = false }
                )
            }
        }
    }
}

@Composable
private fun DeskAuthModal(
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit,
    onCreateAccountClick: () -> Unit
) {
    val dimens = rememberAppDimens()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = if (dimens.isTablet) 480.dp else 400.dp)
                .fillMaxWidth(if (dimens.isTablet) 0.65f else 0.9f)
                .padding(horizontal = dimens.spacingLarge)
                .clickable { /* Consumir clic para no cerrar al tocar el modal */ },
            shape = RoundedCornerShape(dimens.buttonCornerRadius),
            color = Color.White
        ) {
            Box(modifier = Modifier.padding(dimens.spacingLarge)) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(dimens.iconSizeLarge)
                        .background(ButtonDark, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = TextWhite,
                        modifier = Modifier.size(dimens.iconSizeMedium)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(dimens.spacingMedium))
                    Image(
                        painter = painterResource(R.drawable.fondo_texto),
                        contentDescription = null,
                        modifier = Modifier
                            .size(dimens.logoSize)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(dimens.spacingLarge))
                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimens.buttonCornerRadius),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonDark)
                    ) {
                        Text(
                            "Iniciar sesión",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = dimens.fontBody
                        )
                    }
                    Spacer(modifier = Modifier.height(dimens.spacingMedium))
                    Text(
                        text = "¿No tienes una cuenta?",
                        fontSize = dimens.fontBody,
                        color = Color(0xFF6B7B8C)
                    )
                    Spacer(modifier = Modifier.height(dimens.spacingMedium))
                    Button(
                        onClick = onCreateAccountClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimens.buttonCornerRadius),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonDark)
                    ) {
                        Text(
                            "Crear cuenta",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = dimens.fontBody
                        )
                    }
                    Spacer(modifier = Modifier.height(dimens.spacingSmall))
                }
            }
        }
    }
}

@Composable
private fun DeskActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    dimens: com.masin.pangea.presentation.ui.utils.AppDimens
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimens.cardCornerRadius * 1.5f),
        colors = CardDefaults.cardColors(containerColor = CardTealAlt),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.paddingSection),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = dimens.fontSubtitle,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(dimens.spacingSmall))
            Text(
                text = subtitle,
                fontSize = dimens.fontBody,
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
