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
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.masin.pangea.R
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
    val pages = listOf(
        WalkthroughPage(
            title = "Módulo Pangea",
            description = "Explora y conoce todo sobre nuestra empresa. Descubre información clave acerca de nosotros y nuestros servicios.",
            imageRes = R.drawable.conoce,
            dominantColor = Color(0xFF006CBF) // Azul Oscuro
        ),
        WalkthroughPage(
            title = "Módulo E-Learning",
            description = "Entrénate, capacítate y desarrolla nuevas habilidades gestionando tu aprendizaje con nuestras herramientas interactivas.",
            imageRes = R.drawable.gestiona,
            dominantColor = Color(0xFF00A9BF) // Azul Claro
        ),
        WalkthroughPage(
            title = "Módulo Desk",
            description = "Soluciona tus requerimientos técnicos de forma rápida y eficiente mediante este centro de ayuda.",
            imageRes = R.drawable.soluciona,
            dominantColor = Color(0xFF40FF52) // Verde Claro
        ),
        WalkthroughPage(
            title = "Módulo Digiturno",
            description = "Pide y paga tus turnos virtuales sin hacer filas ni perder tiempo, todo desde la comodidad de tu dispositivo.",
            imageRes = R.drawable.paga,
            dominantColor = Color(0xFF40EBFF) // Cian Claro
        ),
        WalkthroughPage(
            title = "Asistente LIA",
            description = "Descubre a LIA, tu asistente virtual inteligente impulsada por IA. Usa comandos de voz para obtener respuestas y navegar por la aplicación instantáneamente.",
            imageRes = R.drawable.lia_avatar,
            dominantColor = PangeaBlue // Azul Pangea
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
                val latam = Locale.forLanguageTag("es-419")
                val langOk = tts.setLanguage(latam)
                if (langOk == TextToSpeech.LANG_MISSING_DATA ||
                    langOk == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts.setLanguage(Locale("es", "ES"))
                }
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Option to Skip
        TextButton(
            onClick = finishAction,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
        ) {
            Text("Omitir", fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            // Content view mapped by pages
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
                            .size(240.dp)
                            .clip(CircleShape)
                            .background(page.dominantColor.copy(alpha = 0.1f))
                            .padding(32.dp),
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

                    Spacer(modifier = Modifier.height(48.dp))
                    
                    Text(
                        text = page.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = PangeaBlue,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = page.description,
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Page indicators
            Row(
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                pages.forEachIndexed { index, _ ->
                    val isSelected = index == currentPage
                    val width = if (isSelected) 24.dp else 10.dp
                    val color = if (isSelected) PangeaCyan else Color.LightGray
                    
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(10.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Controls Siguiente / Anterior
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous button
                if (currentPage > 0) {
                    TextButton(onClick = { currentPage -= 1 }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Anterior",
                            modifier = Modifier.size(20.dp),
                            tint = PangeaBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Anterior", color = PangeaBlue, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(modifier = Modifier.width(100.dp)) // To keep Next button in place relative to flex space
                }

                // Next / Finish button
                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage += 1
                        } else {
                            finishAction()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PangeaCyan
                    ),
                    shape = RoundedCornerShape(24.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = if (currentPage < pages.size - 1) "Siguiente" else "Comenzar",
                        color = PangeaBlue,
                        fontWeight = FontWeight.Bold
                    )
                    if (currentPage < pages.size - 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Siguiente",
                            modifier = Modifier.size(20.dp),
                            tint = PangeaBlue
                        )
                    }
                }
            }
        }

        Image(
            painter = painterResource(id = R.drawable.lia_avatar),
            contentDescription = "LIA",
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 12.dp, bottom = 100.dp)
                .size(96.dp),
            contentScale = ContentScale.Fit
        )
    }
}
