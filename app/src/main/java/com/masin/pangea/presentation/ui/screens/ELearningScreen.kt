package com.masin.pangea.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.masin.pangea.R
import com.masin.pangea.presentation.ui.utils.WindowSizeClass
import com.masin.pangea.presentation.ui.utils.rememberAppDimens
import com.masin.pangea.ui.theme.PANGEAappTheme
import kotlinx.coroutines.launch

// Colores del módulo E-Learning
private val ScreenBackground = Color(0xFFFDFBF7)
private val CardAccentTeal = Color(0xFF5EC4B8)
private val CardBackgroundLight = Color(0xFFF5F5F5)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF424242)
private val PillBorder = Color(0xFFE0E0E0)
private val ConventionLogoRing = Color(0xFFB8DCE8)

private data class ConventionLogoItem(
    val drawableRes: Int,
    val contentDescription: String,
    /** Mismo tono que el fondo del asset para que el círculo se vea homogéneo. */
    val circleBackground: Color
)

// Logos de convenios para el carrusel (circleBackground acoplado al PNG de cada uno)
private val conventionLogos = listOf(
    ConventionLogoItem(
        R.drawable.unisabana,
        "Universidad de La Sabana",
        Color(0xFF1B3F9F)
    ),
    ConventionLogoItem(
        R.drawable.rosario,
        "Universidad del Rosario",
        Color(0xFFD90921)
    ),
    ConventionLogoItem(
        R.drawable.sena,
        "SENA",
        Color(0xFF39A900)
    ),
    ConventionLogoItem(
        R.drawable.andes,
        "Universidad de Los Andes",
        Color(0xFFFFFE00)
    ),
    ConventionLogoItem(
        R.drawable.asturias,
        "Asturias Corporación Universitaria",
        Color(0xFF100F0D)
    ),
    ConventionLogoItem(
        R.drawable.externado,
        "Universidad Externado de Colombia",
        Color(0xFF05442F)
    )
)

// Datos de las tarjetas de cursos
private data class CourseItem(
    val title: String,
    val duration: String?,
    val thumbnailPlaceholder: Int = R.drawable.gestiona,
    val isHighlighted: Boolean = false
)

private val courseItems = listOf(
    CourseItem("Comunicación Asertiva", "5 minutos de contenido"),
    CourseItem("Curso de negociación", "3 minutos de contenido"),
    CourseItem("Manejo de estrés", null),
    CourseItem("Salud ocupacional", "1 minutos de contenido")
)

/**
 * Pantalla del módulo E-Learning con carrusel de convenios y lista de cursos.
 * Adaptativa para móviles y tablets.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ELearningScreen() {
    val dimens = rememberAppDimens()
    var selectedCourseTitle by remember { mutableStateOf<String?>(null) }

    selectedCourseTitle?.let { title ->
        ComingSoonCourseDialog(
            courseTitle = title,
            onDismiss = { selectedCourseTitle = null }
        )
    }

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
                .verticalScroll(rememberScrollState())
        ) {
            // Sección superior: título y descripción
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dimens.paddingSection + horizontalPad, vertical = dimens.spacingLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "E-LEARNING",
                    fontSize = dimens.fontTitle,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(dimens.spacingMedium))
                Text(
                    text = "En este espacio encontrarás toda la información que necesitas.",
                    fontSize = dimens.fontBody,
                    color = TextGray,
                    lineHeight = dimens.lineHeightBody,
                    textAlign = TextAlign.Center
                )
            }

            // Sección Convenios: carrusel de logos
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPad),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Convenios",
                    fontSize = dimens.fontSubtitle,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.padding(bottom = dimens.spacingMedium)
                )

                // Carrusel infinito: repetimos los items muchas veces y empezamos en el centro
                val repeatCount = 200
                val totalItems = repeatCount * conventionLogos.size
                val initialIndex = totalItems / 2
                val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
                val scope = rememberCoroutineScope()
                val itemSize = dimens.circleItemSize

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
                            modifier = Modifier.size(dimens.iconSizeLarge),
                            tint = Color.Black
                        )
                    }

                    LazyRow(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .height(itemSize + dimens.spacingMedium),
                        horizontalArrangement = Arrangement.spacedBy(dimens.spacingMedium),
                        contentPadding = PaddingValues(horizontal = dimens.spacingSmall)
                    ) {
                        items(totalItems, key = { it }) { index ->
                            val actualIndex = index % conventionLogos.size
                            val item = conventionLogos[actualIndex]
                            Box(
                                modifier = Modifier
                                    .size(itemSize)
                                    .clip(CircleShape)
                                    .background(item.circleBackground)
                                    .border(1.dp, ConventionLogoRing, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                                        .data(item.drawableRes)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = item.contentDescription,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding((itemSize.value * 0.08f).dp),
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
                            modifier = Modifier.size(dimens.iconSizeLarge),
                            tint = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimens.spacingLarge))

            // Lista de tarjetas de contenido
            // En tablet: grid de 2 columnas
            if (dimens.isTablet) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.paddingScreen + horizontalPad),
                    verticalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
                ) {
                    val rows = courseItems.chunked(2)
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
                        ) {
                            rowItems.forEach { course ->
                                Box(modifier = Modifier.weight(1f)) {
                                    CourseCard(
                                        title = course.title,
                                        duration = course.duration,
                                        thumbnailResId = course.thumbnailPlaceholder,
                                        isHighlighted = course.isHighlighted,
                                        onClick = { selectedCourseTitle = course.title },
                                        dimens = dimens
                                    )
                                }
                            }
                            // Si la fila tiene solo 1 item, añadir spacer para equilibrar
                            if (rowItems.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.paddingScreen),
                    verticalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
                ) {
                    courseItems.forEach { course ->
                        CourseCard(
                            title = course.title,
                            duration = course.duration,
                            thumbnailResId = course.thumbnailPlaceholder,
                            isHighlighted = course.isHighlighted,
                            onClick = { selectedCourseTitle = course.title },
                            dimens = dimens
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(dimens.bottomBarSpacing))
        }
    }
}

@Composable
private fun ComingSoonCourseDialog(
    courseTitle: String,
    onDismiss: () -> Unit
) {
    val dimens = rememberAppDimens()
    val scaleAnim = remember(courseTitle) { Animatable(0.88f) }
    LaunchedEffect(courseTitle) {
        scaleAnim.snapTo(0.88f)
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    val pulse = rememberInfiniteTransition(label = "comingSoonPulse")
    val iconPulse by pulse.animateFloat(
        initialValue = 0.94f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconPulse"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Surface(
            modifier = Modifier
                .padding(horizontal = dimens.spacingLarge)
                .widthIn(max = if (dimens.isTablet) 480.dp else 400.dp)
                .fillMaxWidth()
                .graphicsLayer {
                    scaleX = scaleAnim.value
                    scaleY = scaleAnim.value
                },
            shape = RoundedCornerShape(22.dp),
            color = Color.White,
            shadowElevation = 10.dp
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Cerrar",
                        tint = TextGray
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimens.spacingLarge)
                        .padding(top = dimens.paddingSection, bottom = dimens.spacingLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(dimens.thumbnailSize)
                            .graphicsLayer {
                                scaleX = iconPulse
                                scaleY = iconPulse
                            }
                            .clip(CircleShape)
                            .background(CardAccentTeal.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(dimens.iconSizeLarge),
                            tint = CardAccentTeal
                        )
                    }

                    Spacer(modifier = Modifier.height(dimens.spacingMedium))

                    Text(
                        text = "Próximamente",
                        fontSize = dimens.fontCaption,
                        fontWeight = FontWeight.SemiBold,
                        color = CardAccentTeal,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(dimens.spacingSmall))

                    Text(
                        text = courseTitle,
                        fontSize = dimens.fontSubtitle,
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(dimens.spacingMedium))

                    Text(
                        text = "Contenido disponible próximamente",
                        fontSize = dimens.fontBody,
                        lineHeight = dimens.lineHeightBody,
                        color = TextGray,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(dimens.spacingLarge))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(dimens.cardCornerRadius),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CardAccentTeal,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Text(
                            text = "Entendido",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = dimens.fontBody,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseCard(
    title: String,
    duration: String?,
    thumbnailResId: Int,
    isHighlighted: Boolean,
    onClick: () -> Unit,
    dimens: com.masin.pangea.presentation.ui.utils.AppDimens
) {
    val cardBackground = if (isHighlighted) CardAccentTeal.copy(alpha = 0.15f) else CardBackgroundLight
    val context = androidx.compose.ui.platform.LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimens.cardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimens.paddingCard),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Miniatura
            Box(
                modifier = Modifier
                    .size(dimens.thumbnailSize)
                    .clip(RoundedCornerShape(dimens.spacingSmall))
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

            Spacer(modifier = Modifier.width(dimens.spacingMedium))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayCircleOutline,
                        contentDescription = null,
                        modifier = Modifier.size(dimens.iconSizeSmall),
                        tint = TextGray
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = title,
                        fontSize = dimens.fontBody,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                }

                Spacer(modifier = Modifier.height(dimens.spacingSmall))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(dimens.spacingSmall)
                ) {
                    PillChip(text = "Definición", dimens = dimens)
                    duration?.let { PillChip(text = it, dimens = dimens) }
                }
            }
        }
    }
}

@Composable
private fun PillChip(
    text: String,
    dimens: com.masin.pangea.presentation.ui.utils.AppDimens
) {
    Box(
        modifier = Modifier
            .border(1.dp, PillBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = (10f * dimens.scaleFactor.coerceIn(0.9f, 1.3f)).dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            fontSize = dimens.fontCaption,
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
