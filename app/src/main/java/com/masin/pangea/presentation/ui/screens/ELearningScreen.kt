package com.masin.pangea.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.masin.pangea.R
import com.masin.pangea.ui.theme.PANGEAappTheme
import kotlinx.coroutines.launch

// Colores del módulo E-Learning
private val ScreenBackground = Color(0xFFFDFBF7)
private val CardAccentTeal = Color(0xFF5EC4B8)
private val CardBackgroundLight = Color(0xFFF5F5F5)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF424242)
private val PillBorder = Color(0xFFE0E0E0)

// Logos de convenios para el carrusel
private val conventionLogos = listOf(
    R.drawable.unisabana to "Universidad de La Sabana",
    R.drawable.rosario to "Universidad del Rosario",
    R.drawable.sena to "SENA",
    R.drawable.andes to "Universidad de Los Andes",
    R.drawable.asturias to "Asturias Corporación Universitaria",
    R.drawable.externado to "Universidad Externado de Colombia"
)

// Datos de las tarjetas de cursos
private data class CourseItem(
    val title: String,
    val duration: String?,
    val thumbnailPlaceholder: Int = R.drawable.gestiona,
    val isHighlighted: Boolean = false
)

private val courseItems = listOf(
    CourseItem("Comunicación Asertiva", "5 minutos de contenido", isHighlighted = true),
    CourseItem("Curso de negociación", "3 minutos de contenido"),
    CourseItem("Manejo de estrés", null),
    CourseItem("Salud ocupacional", "1 minutos de contenido")
)

/**
 * Pantalla del módulo E-Learning con carrusel de convenios y lista de cursos.
 */
@Composable
fun ELearningScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .verticalScroll(rememberScrollState())
    ) {
        // Sección superior: título y descripción
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "E-LEARNING",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "En este espacio encontrarás toda la información que necesitas para cumplir con tus obligaciones tributarias. Infórmate sobre los impuestos distritales, plazos, beneficios y normatividad vigente.",
                fontSize = 14.sp,
                color = TextGray,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )
        }

        // Sección Convenios: carrusel de logos
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Convenios",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Carrusel infinito: repetimos los items muchas veces y empezamos en el centro
            val repeatCount = 500
            val totalItems = repeatCount * conventionLogos.size
            val initialIndex = totalItems / 2
            val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
            val scope = rememberCoroutineScope()
            val itemSize = 120.dp

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = {
                        val currentIndex = listState.firstVisibleItemIndex
                        scope.launch {
                            listState.animateScrollToItem((currentIndex - 1 + totalItems) % totalItems)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Anterior",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                }

                LazyRow(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .height(itemSize + 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(totalItems, key = { it }) { index ->
                        val actualIndex = index % conventionLogos.size
                        val (drawableRes, contentDesc) = conventionLogos[actualIndex]
                        Box(
                            modifier = Modifier
                                .size(itemSize)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                    .data(drawableRes)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = contentDesc,
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .fillMaxSize(0.9f),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        val currentIndex = listState.firstVisibleItemIndex
                        scope.launch {
                            listState.animateScrollToItem((currentIndex + 1) % totalItems)
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Siguiente",
                        modifier = Modifier.size(32.dp),
                        tint = Color.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Lista de tarjetas de contenido
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            courseItems.forEach { course ->
                CourseCard(
                    title = course.title,
                    duration = course.duration,
                    thumbnailResId = course.thumbnailPlaceholder,
                    isHighlighted = course.isHighlighted
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun CourseCard(
    title: String,
    duration: String?,
    thumbnailResId: Int,
    isHighlighted: Boolean
) {
    val cardBackground = if (isHighlighted) CardAccentTeal.copy(alpha = 0.15f) else CardBackgroundLight
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: navegar al contenido del curso */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Miniatura
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(thumbnailResId)
                        .crossfade(true)
                        .build(),
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = TextGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PillChip(text = "Definición")
                    duration?.let { PillChip(text = it) }
                }
            }
        }
    }
}

@Composable
private fun PillChip(text: String) {
    Box(
        modifier = Modifier
            .border(1.dp, PillBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = TextGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ELearningScreenPreview() {
    PANGEAappTheme {
        ELearningScreen()
    }
}
