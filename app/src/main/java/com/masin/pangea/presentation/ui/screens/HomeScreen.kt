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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.masin.pangea.R
import com.masin.pangea.ui.theme.PANGEAappTheme

// Colores para las tarjetas según el diseño (franja superior e iconos)
private val PangeaAccent = Color(0xFF7aff40)      // Azul
private val ELearningAccent = Color(0xFF40ebff)   // Verde lima
private val DeskAccent = Color(0xFF006cbf)        // Verde
private val DigiturnoAccent = Color(0xFF3FFF91)   // Cian

// Fondos suaves para el contenido de cada tarjeta (tintes ligeros)
private val PangeaBackground = Color(0xFFF0FFE8)      // Azul muy suave
private val ELearningBackground = Color(0xFFE8F4FC)   // Verde lima muy suave
private val DeskBackground = Color(0xFFE8FFFC)        // Verde muy suave
private val DigiturnoBackground = Color(0xFFF0FFE8)   // Cian muy suave

private val TextGray = Color(0xFF424242)
private val BannerBackground = Color(0xFF7AFF40)

/**
 * Pantalla de inicio con banner y tarjetas de navegación
 */
@Composable
fun HomeScreen(
    onNavigateToPangea: () -> Unit,
    onNavigateToELearning: () -> Unit,
    onNavigateToDesk: () -> Unit,
    onNavigateToDigiturno: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val isTablet = screenWidthDp >= 600
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        // Banner GIF - en móviles: altura fija con crop; en tablets: se muestra completo con lateral del color del banner
        if (isTablet) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(BannerBackground),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(R.drawable.banner)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Banner Portal del Contribuyente",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Fit
                )
            }
        } else {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(R.drawable.banner)
                    .crossfade(true)
                    .build(),
                contentDescription = "Banner Portal del Contribuyente",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Título "Ruta del Contribuyente"
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pangea App",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "⭐",
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Aquí encontrarás todo lo que necesitas.",
                fontSize = 14.sp,
                color = TextGray,
                lineHeight = 20.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Grid de tarjetas 2x2
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Primera fila: Pangea y E - Learning
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NavigationCard(
                    modifier = Modifier.weight(1f).height(200.dp),
                    title = "Pangea",
                    description = "Descubre nuestro ecosistema de ciudadanía digital que conecta personas, empresarios y comunidades.",
                    iconResId = R.drawable.conoce,
                    accentColor = PangeaAccent,
                    backgroundColor = PangeaBackground,
                    onClick = onNavigateToPangea
                )
                
                NavigationCard(
                    modifier = Modifier.weight(1f).height(200.dp),
                    title = "E-Learning",
                    description = "Forma tu talento y accede a recursos de aprendizaje del Grupo Masin.",
                    iconResId = R.drawable.gestiona,
                    accentColor = ELearningAccent,
                    backgroundColor = ELearningBackground,
                    onClick = onNavigateToELearning
                )
            }
            
            // Segunda fila: Digiturno y Desk
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                NavigationCard(
                    modifier = Modifier.weight(1f).height(200.dp),
                    title = "Desk",
                    description = "Consulta la pantalla de turnos y administra tu atención.",
                    iconResId = R.drawable.paga,
                    accentColor = DeskAccent,
                    backgroundColor = DeskBackground,
                    onClick = onNavigateToDesk
                )

                NavigationCard(
                    modifier = Modifier.weight(1f).height(200.dp),
                    title = "Digiturno",
                    description = "Solicita tu turno de forma ágil y evita filas.",
                    iconResId = R.drawable.soluciona,
                    accentColor = DigiturnoAccent,
                    backgroundColor = DigiturnoBackground,
                    onClick = onNavigateToDigiturno
                )

            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Tarjeta de navegación reutilizable con franja superior de color
 */
@Composable
private fun NavigationCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    iconResId: Int,
    accentColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
        ) {
            // Franja superior de color con icono y título (blanco para contraste)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentColor)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconResId),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            // Contenido principal con fondo suave de color
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(backgroundColor)
                    .padding(12.dp)
            ) {
                // Descripción (maxLines para que el botón "Ir" siempre sea visible)
                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = TextGray,
                    lineHeight = 18.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Botón "Ir" con borde del color de acento
                Row(
                    modifier = Modifier
                        .align(Alignment.End)
                        .border(
                            width = 1.5.dp,
                            color = accentColor,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Ir",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = accentColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "→",
                        fontSize = 16.sp,
                        color = accentColor
                    )
                }
            }
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

@Preview(showBackground = true)
@Composable
fun NavigationCardPreview() {
    PANGEAappTheme {
        NavigationCard(
            modifier = Modifier.height(200.dp),
            title = "Pangea",
            description = "Descubre nuestro ecosistema de ciudadanía digital que conecta personas, empresarios y comunidades.",
            iconResId = R.drawable.conoce,
            accentColor = PangeaAccent,
            backgroundColor = PangeaBackground,
            onClick = {}
        )
    }
}
