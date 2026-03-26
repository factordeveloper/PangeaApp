package com.masin.pangea.presentation.ui.screens

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.masin.pangea.R
import com.masin.pangea.data.config.PangeaTtsVoices
import com.masin.pangea.presentation.ui.utils.WindowSizeClass
import com.masin.pangea.presentation.ui.utils.rememberAppDimens
import com.masin.pangea.ui.theme.*

data class WalkthroughPage(
    val title: String,
    val description: String,
    val imageRes: Int,
    val dominantColor: Color
)

@Composable
fun WalkthroughScreen(
    onFinishWalkthrough: () -> Unit
) {
    val context = LocalContext.current
    val dimens = rememberAppDimens()

    val pages = listOf(
        WalkthroughPage(
            title = "Módulo Pangea",
            description = "Explora y conoce todo sobre nuestra empresa. Descubre información clave acerca de nosotros y nuestros servicios.",
            imageRes = R.drawable.conoce,
            dominantColor = Color(0xFF006CBF)
        ),
        WalkthroughPage(
            title = "Módulo E-Learning",
            description = "Entrénate, capacítate y desarrolla nuevas habilidades gestionando tu aprendizaje con nuestras herramientas interactivas.",
            imageRes = R.drawable.gestiona,
            dominantColor = Color(0xFF00A9BF)
        ),
        WalkthroughPage(
            title = "Módulo Desk",
            description = "Soluciona tus requerimientos técnicos de forma rápida y eficiente mediante este centro de ayuda.",
            imageRes = R.drawable.soluciona,
            dominantColor = Color(0xFF40FF52)
        ),
        WalkthroughPage(
            title = "Módulo Digiturno",
            description = "Pide y paga tus turnos virtuales sin hacer filas ni perder tiempo, todo desde la comodidad de tu dispositivo.",
            imageRes = R.drawable.paga,
            dominantColor = Color(0xFF40EBFF)
        ),
        WalkthroughPage(
            title = "Asistente LIA",
            description = "Descubre a LIA, tu asistente virtual inteligente impulsada por IA. Usa comandos de voz para obtener respuestas y navegar por la aplicación instantáneamente.",
            imageRes = R.drawable.lia_avatar,
            dominantColor = PangeaBlue
        )
    )

    var currentPage by remember { mutableStateOf(0) }
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val engineHolder = arrayOfNulls<TextToSpeech>(1)
        engineHolder[0] = TextToSpeech(context, onTtsInit@ { status ->
            val tts = engineHolder[0] ?: return@onTtsInit
            if (status == TextToSpeech.SUCCESS) {
                PangeaTtsVoices.apply(tts, PangeaTtsVoices.walkthrough)
                ttsReady = true
            }
        })
        val engine = engineHolder[0]!!
        ttsEngine = engine
        onDispose {
            engine.stop()
            engine.shutdown()
            ttsEngine = null
            ttsReady = false
        }
    }

    LaunchedEffect(currentPage, ttsReady) {
        val tts = ttsEngine ?: return@LaunchedEffect
        if (!ttsReady) return@LaunchedEffect
        val page = pages[currentPage]
        val utterance = "${page.title}. ${page.description}"
        tts.speak(utterance, TextToSpeech.QUEUE_FLUSH, null, "walkthrough_$currentPage")
    }

    val finishAction = {
        ttsEngine?.stop()
        val sharedPrefs = context.getSharedPreferences("pangea_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("is_first_run", false).apply()
        onFinishWalkthrough()
    }

    // Dimensiones adaptativas
    val circleImageSize = dimens.heroImageSize
    val topPadding = if (dimens.isTablet) 40.dp else 60.dp
    val isExpanded = dimens.windowSizeClass == WindowSizeClass.EXPANDED

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Skip button
        TextButton(
            onClick = finishAction,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = topPadding, end = dimens.paddingScreen),
            colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
        ) {
            Text("Omitir", fontWeight = FontWeight.Bold, fontSize = dimens.fontBody)
        }

        // Layout responsivo: columna en portrait/compact, fila (2 columnas) en expanded
        if (isExpanded) {
            // TABLET EXPANDED: imagen a la izquierda, texto + controles a la derecha
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topPadding + 32.dp, bottom = 32.dp,
                        start = dimens.paddingSection, end = dimens.paddingSection)
            ) {
                Crossfade(
                    targetState = currentPage,
                    animationSpec = tween(400),
                    label = "page_transition"
                ) { pageIndex ->
                    val page = pages[pageIndex]
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(dimens.spacingXLarge),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Imagen circular (izquierda)
                        Box(
                            modifier = Modifier
                                .size(circleImageSize)
                                .clip(CircleShape)
                                .background(page.dominantColor.copy(alpha = 0.1f))
                                .padding(dimens.spacingLarge),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = page.imageRes),
                                contentDescription = page.title,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(if (page.imageRes == R.drawable.lia_avatar) CircleShape else RoundedCornerShape(0.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }

                        // Texto + controles (derecha)
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = page.title,
                                fontSize = dimens.fontHero,
                                fontWeight = FontWeight.Bold,
                                color = PangeaBlue
                            )
                            Spacer(modifier = Modifier.height(dimens.spacingLarge))
                            Text(
                                text = page.description,
                                fontSize = dimens.fontBody,
                                color = Color.DarkGray,
                                lineHeight = dimens.lineHeightBody
                            )
                            Spacer(modifier = Modifier.height(dimens.spacingXLarge))

                            // Page indicators
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                pages.forEachIndexed { index, _ ->
                                    val isSelected = index == currentPage
                                    Box(
                                        modifier = Modifier
                                            .height(10.dp)
                                            .width(if (isSelected) 24.dp else 10.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) PangeaCyan else Color.LightGray)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(dimens.spacingLarge))

                            // Navigation buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (currentPage > 0) {
                                    TextButton(onClick = { currentPage -= 1 }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Anterior",
                                            modifier = Modifier.size(dimens.iconSizeMedium),
                                            tint = PangeaBlue
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Anterior", color = PangeaBlue, fontWeight = FontWeight.Bold, fontSize = dimens.fontBody)
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(100.dp))
                                }
                                Button(
                                    onClick = {
                                        if (currentPage < pages.size - 1) currentPage += 1
                                        else finishAction()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = PangeaCyan),
                                    shape = RoundedCornerShape(dimens.buttonCornerRadius),
                                    contentPadding = PaddingValues(horizontal = dimens.spacingLarge, vertical = dimens.spacingMedium)
                                ) {
                                    Text(
                                        text = if (currentPage < pages.size - 1) "Siguiente" else "Comenzar",
                                        color = PangeaBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = dimens.fontBody
                                    )
                                    if (currentPage < pages.size - 1) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = "Siguiente",
                                            modifier = Modifier.size(dimens.iconSizeMedium),
                                            tint = PangeaBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // COMPACT / MEDIUM: layout vertical original (adaptado con AppDimens)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = topPadding + 32.dp, bottom = 32.dp,
                        start = dimens.paddingScreen, end = dimens.paddingScreen),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Crossfade(
                    targetState = currentPage,
                    animationSpec = tween(400),
                    label = "page_transition",
                    modifier = Modifier.weight(1f)
                ) { pageIndex ->
                    val page = pages[pageIndex]
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(circleImageSize)
                                .clip(CircleShape)
                                .background(page.dominantColor.copy(alpha = 0.1f))
                                .padding(dimens.spacingLarge),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = page.imageRes),
                                contentDescription = page.title,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(if (page.imageRes == R.drawable.lia_avatar) CircleShape else RoundedCornerShape(0.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(dimens.spacingXLarge))

                        Text(
                            text = page.title,
                            fontSize = dimens.fontTitle,
                            fontWeight = FontWeight.Bold,
                            color = PangeaBlue,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(dimens.spacingLarge))

                        Text(
                            text = page.description,
                            fontSize = dimens.fontBody,
                            color = Color.DarkGray,
                            textAlign = TextAlign.Center,
                            lineHeight = dimens.lineHeightBody,
                            modifier = Modifier.padding(horizontal = dimens.spacingMedium)
                        )
                    }
                }

                // Page indicators
                Row(
                    modifier = Modifier
                        .padding(vertical = dimens.spacingLarge)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    pages.forEachIndexed { index, _ ->
                        val isSelected = index == currentPage
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(10.dp)
                                .width(if (isSelected) 24.dp else 10.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) PangeaCyan else Color.LightGray)
                        )
                    }
                }

                // Navigation buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentPage > 0) {
                        TextButton(onClick = { currentPage -= 1 }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Anterior",
                                modifier = Modifier.size(dimens.iconSizeMedium),
                                tint = PangeaBlue
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Anterior", color = PangeaBlue, fontWeight = FontWeight.Bold, fontSize = dimens.fontBody)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(100.dp))
                    }

                    Button(
                        onClick = {
                            if (currentPage < pages.size - 1) currentPage += 1
                            else finishAction()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PangeaCyan),
                        shape = RoundedCornerShape(dimens.buttonCornerRadius),
                        contentPadding = PaddingValues(horizontal = dimens.spacingLarge, vertical = dimens.spacingMedium)
                    ) {
                        Text(
                            text = if (currentPage < pages.size - 1) "Siguiente" else "Comenzar",
                            color = PangeaBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = dimens.fontBody
                        )
                        if (currentPage < pages.size - 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Siguiente",
                                modifier = Modifier.size(dimens.iconSizeMedium),
                                tint = PangeaBlue
                            )
                        }
                    }
                }
            }
        }

        // Avatar de LIA en la esquina inferior (solo en layout vertical)
        if (!isExpanded) {
            Image(
                painter = painterResource(id = R.drawable.lia_avatar),
                contentDescription = "LIA",
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = dimens.spacingXLarge + 40.dp)
                    .size(dimens.avatarSize * 2f),
                contentScale = ContentScale.Fit
            )
        }
    }
}
