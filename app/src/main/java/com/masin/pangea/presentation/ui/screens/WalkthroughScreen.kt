package com.masin.pangea.presentation.ui.screens

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.masin.pangea.R
import com.masin.pangea.data.config.PangeaTtsVoices
import com.masin.pangea.presentation.ui.utils.rememberAppDimens
import com.masin.pangea.ui.theme.*
import kotlinx.coroutines.delay

// ─────────────────────────────────────────────────────────────
//  Data model — each "stop" LIA visits
// ─────────────────────────────────────────────────────────────

private data class LiaStop(
    val title: String,
    val description: String,
    val ttsText: String,           // what TTS will say
    val dominantColor: Color,
    val accentColor: Color,
    val liaFractionX: Float,       // 0..1 horizontal position on screen
    val liaFractionY: Float,       // 0..1 vertical position on screen
    val bubbleAlignEnd: Boolean,   // true = bubble aligns to right, false = left
    val durationMs: Long = 5500L   // how long to stay at this stop (ms)
)

// ─────────────────────────────────────────────────────────────
//  Main composable
// ─────────────────────────────────────────────────────────────

@Composable
fun WalkthroughScreen(
    onFinishWalkthrough: () -> Unit
) {
    val context = LocalContext.current
    val dimens = rememberAppDimens()

    // Narration & position stops for LIA — feels like a real guided tour
    val stops = remember {
        listOf(
            LiaStop(
                title = "¡Hola! Soy LIA 👋",
                description = "Tu guía virtual en Pangea. Déjame mostrarte todo lo que puedes hacer aquí.",
                ttsText = "¡Hola! Soy LIA, tu guía virtual en Pangea. Déjame mostrarte todo lo que puedes hacer aquí.",
                dominantColor = PangeaBlue,
                accentColor = PangeaCyan,
                liaFractionX = 0.50f,
                liaFractionY = 0.32f,
                bubbleAlignEnd = false,
                durationMs = 5000L
            ),
            LiaStop(
                title = "Módulo Pangea 🏢",
                description = "Explora quiénes somos, nuestros servicios y todo lo que nuestra empresa tiene para ofrecerte.",
                ttsText = "En el Módulo Pangea encontrarás información clave sobre nuestra empresa y sus servicios.",
                dominantColor = Color(0xFF006CBF),
                accentColor = Color(0xFF40EBFF),
                liaFractionX = 0.78f,
                liaFractionY = 0.12f,
                bubbleAlignEnd = true,
                durationMs = 5500L
            ),
            LiaStop(
                title = "Módulo E-Learning 📚",
                description = "Capacítate y desarrolla nuevas habilidades con herramientas interactivas de aprendizaje.",
                ttsText = "Con el módulo de E-Learning puedes capacitarte y desarrollar nuevas habilidades a tu ritmo.",
                dominantColor = Color(0xFF00A9BF),
                accentColor = Color(0xFF40FF52),
                liaFractionX = 0.08f,
                liaFractionY = 0.14f,
                bubbleAlignEnd = false,
                durationMs = 5500L
            ),
            LiaStop(
                title = "Módulo Desk 🛠️",
                description = "Resuelve tus requerimientos técnicos de forma rápida y eficiente desde este centro de ayuda.",
                ttsText = "El módulo Desk es tu centro de soporte técnico. Resuelve requerimientos de forma rápida y eficiente.",
                dominantColor = Color(0xFF1A7A3C),
                accentColor = Color(0xFF40FF52),
                liaFractionX = 0.72f,
                liaFractionY = 0.68f,
                bubbleAlignEnd = true,
                durationMs = 5500L
            ),
            LiaStop(
                title = "Módulo Digiturno 🎫",
                description = "Pide y paga turnos virtuales sin filas, todo desde la comodidad de tu dispositivo.",
                ttsText = "Con Digiturno puedes pedir y pagar tus turnos virtuales sin hacer filas ni perder tiempo.",
                dominantColor = Color(0xFF0B6B6B),
                accentColor = Color(0xFF40EBFF),
                liaFractionX = 0.08f,
                liaFractionY = 0.66f,
                bubbleAlignEnd = false,
                durationMs = 5500L
            ),
            LiaStop(
                title = "¡Estoy aquí para ayudarte! 🤖",
                description = "Usa comandos de voz en cualquier momento para obtener respuestas y navegar al instante.",
                ttsText = "Y recuerda, soy LIA. Estoy siempre disponible con comandos de voz para ayudarte a navegar la app.",
                dominantColor = PangeaBlue,
                accentColor = PangeaCyan,
                liaFractionX = 0.50f,
                liaFractionY = 0.38f,
                bubbleAlignEnd = true,
                durationMs = 5500L
            )
        )
    }

    // ── State ─────────────────────────────────────────────────
    var currentStop by remember { mutableIntStateOf(0) }
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }
    var displayedText by remember { mutableStateOf("") }
    var progressFraction by remember { mutableFloatStateOf(0f) }

    val stop = stops[currentStop]

    // ── TTS setup ─────────────────────────────────────────────
    DisposableEffect(Unit) {
        val holder = arrayOfNulls<TextToSpeech>(1)
        holder[0] = TextToSpeech(context, onTtsInit@{ status ->
            val tts = holder[0] ?: return@onTtsInit
            if (status == TextToSpeech.SUCCESS) {
                PangeaTtsVoices.apply(tts, PangeaTtsVoices.walkthrough)
                ttsReady = true
            }
        })
        val engine = holder[0]!!
        ttsEngine = engine
        onDispose {
            engine.stop()
            engine.shutdown()
            ttsEngine = null
            ttsReady = false
        }
    }

    // ── Finish helper ─────────────────────────────────────────
    val finishAction = remember(context, onFinishWalkthrough) {
        {
            ttsEngine?.stop()
            context.getSharedPreferences("pangea_prefs", Context.MODE_PRIVATE)
                .edit().putBoolean("is_first_run", false).apply()
            onFinishWalkthrough()
        }
    }

    // ── Auto-advance loop — the main engine of the screen ─────
    LaunchedEffect(ttsReady) {
        // Wait for TTS
        while (!ttsReady) delay(100)

        for (idx in stops.indices) {
            currentStop = idx
            val s = stops[idx]
            progressFraction = 0f
            displayedText = ""

            // TTS narration
            ttsEngine?.speak(s.ttsText, TextToSpeech.QUEUE_FLUSH, null, "lia_stop_$idx")

            // Short pause so LIA can fly to position first
            delay(400)

            // Typewriter — reveal description text
            for (i in s.description.indices) {
                displayedText = s.description.substring(0, i + 1)
                delay(22)
            }

            // Animate progress bar filling up for the remaining duration
            val textDuration = s.description.length * 22L + 400L
            val remaining = (s.durationMs - textDuration).coerceAtLeast(1200L)
            val steps = 60
            val stepMs = remaining / steps
            for (step in 1..steps) {
                progressFraction = step.toFloat() / steps
                delay(stepMs)
            }
        }

        // All stops visited → finish
        finishAction()
    }

    // ── LIA adaptive size ─────────────────────────────────────
    val liaSize = if (dimens.isTablet) 110.dp else 82.dp
    val liaSizePx = if (dimens.isTablet) 110f else 82f

    // ── LIA animated position ─────────────────────────────────
    val targetX = (dimens.screenWidthDp - liaSizePx) * stop.liaFractionX
    val targetY = (dimens.screenHeightDp - liaSizePx) * stop.liaFractionY

    val liaX by animateFloatAsState(
        targetValue = targetX,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessLow),
        label = "liaX"
    )
    val liaY by animateFloatAsState(
        targetValue = targetY,
        animationSpec = spring(dampingRatio = 0.62f, stiffness = Spring.StiffnessLow),
        label = "liaY"
    )

    // ── LIA perpetual animations ──────────────────────────────
    val infinite = rememberInfiniteTransition(label = "lia")
    val floatY by infinite.animateFloat(
        initialValue = -7f, targetValue = 7f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float"
    )
    val pulse by infinite.animateFloat(
        initialValue = 1.00f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(950, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val glowA by infinite.animateFloat(
        initialValue = 0.12f, targetValue = 0.42f,
        animationSpec = infiniteRepeatable(tween(1300, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    // ── LIA tilt direction ────────────────────────────────────
    var prevStop by remember { mutableIntStateOf(0) }
    val tiltAngle by animateFloatAsState(
        targetValue = if (currentStop > prevStop) 20f else if (currentStop < prevStop) -20f else 0f,
        animationSpec = tween(600),
        label = "tilt"
    )
    LaunchedEffect(currentStop) {
        delay(700)
        prevStop = currentStop
    }

    // ── Animated colors ───────────────────────────────────────
    val bgColor by animateColorAsState(stop.dominantColor.copy(alpha = 0.10f), tween(800), label = "bg")
    val accentBg by animateColorAsState(stop.accentColor.copy(alpha = 0.05f), tween(800), label = "acc")
    val dotColor by animateColorAsState(stop.accentColor, tween(500), label = "dot")

    // ─────────────────────────────────────────────────────────
    //  UI
    // ─────────────────────────────────────────────────────────
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color.White, bgColor, accentBg, Color.White)
                )
            )
    ) {

        // Decorative ambient blobs
        AmbientBlobs(stop.dominantColor, stop.accentColor)

        // ── Progress bar — top ────────────────────────────────
        ProgressStrip(
            fraction = progressFraction,
            stopIndex = currentStop,
            total = stops.size,
            activeColor = dotColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 52.dp)
                .zIndex(15f)
        )

        // ── "Omitir" button ───────────────────────────────────
        TextButton(
            onClick = finishAction,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 44.dp, end = dimens.paddingScreen)
                .zIndex(20f),
            colors = ButtonDefaults.textButtonColors(
                contentColor = stop.dominantColor.copy(alpha = 0.75f)
            )
        ) {
            Text("Omitir", fontWeight = FontWeight.Bold, fontSize = dimens.fontBody)
        }

        // ── Center card — module info ─────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimens.paddingScreen),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = currentStop,
                transitionSpec = {
                    fadeIn(tween(500)).togetherWith(fadeOut(tween(350)))
                },
                label = "stopContent"
            ) { idx ->
                val s = stops[idx]
                CenterInfoCard(
                    stop = s,
                    displayedText = displayedText,
                    isActive = idx == currentStop
                )
            }
        }

        // ── LIA flying avatar ─────────────────────────────────
        Box(
            modifier = Modifier
                .offset(x = liaX.dp, y = (liaY + floatY).dp)
                .zIndex(30f)
                .size(liaSize + 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.fillMaxSize()) {
                drawGlowRing(stop.accentColor, glowA)
            }
            Image(
                painter = painterResource(R.drawable.lia_avatar),
                contentDescription = "LIA",
                modifier = Modifier
                    .size(liaSize)
                    .scale(pulse)
                    .rotate(tiltAngle)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Progress strip — dots + filling bar
// ─────────────────────────────────────────────────────────────

@Composable
private fun ProgressStrip(
    fraction: Float,
    stopIndex: Int,
    total: Int,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dot indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until total) {
                val isActive = i == stopIndex
                val isPast = i < stopIndex
                val dotWidth by animateDpAsState(
                    targetValue = if (isActive) 24.dp else 8.dp,
                    animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium),
                    label = "dw"
                )
                val dotColor by animateColorAsState(
                    targetValue = when {
                        isActive -> activeColor
                        isPast   -> activeColor.copy(alpha = 0.45f)
                        else     -> Color(0xFFDDE0E6)
                    },
                    animationSpec = tween(350),
                    label = "dc"
                )
                Box(
                    Modifier
                        .height(7.dp)
                        .width(dotWidth)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        // Slim progress track for current stop
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(3.dp)
                .clip(CircleShape)
                .background(Color(0xFFDDE0E6))
        ) {
            val animatedFraction by animateFloatAsState(
                targetValue = fraction,
                animationSpec = tween(400),
                label = "prog"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedFraction)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(listOf(activeColor.copy(0.7f), activeColor))
                    )
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Center card — title + typewriter bubble
// ─────────────────────────────────────────────────────────────

@Composable
private fun CenterInfoCard(
    stop: LiaStop,
    displayedText: String,
    isActive: Boolean
) {
    val cursor by rememberInfiniteTransition(label = "c").animateFloat(
        initialValue = 1f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(520), RepeatMode.Reverse),
        label = "blink"
    )
    val shownText = if (isActive && displayedText.length < stop.description.length)
        "$displayedText${if (cursor > 0.5f) "▎" else " "}"
    else
        displayedText

    Column(
        horizontalAlignment = if (stop.bubbleAlignEnd) Alignment.End else Alignment.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Title pill badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(stop.dominantColor, stop.accentColor.copy(0.85f))
                    )
                )
                .padding(horizontal = 22.dp, vertical = 9.dp)
        ) {
            Text(
                text = stop.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.height(12.dp))

        // Speech bubble
        Surface(
            shape = RoundedCornerShape(
                topStart = if (stop.bubbleAlignEnd) 18.dp else 4.dp,
                topEnd = 18.dp,
                bottomStart = 18.dp,
                bottomEnd = if (stop.bubbleAlignEnd) 4.dp else 18.dp
            ),
            color = Color.White.copy(alpha = 0.97f),
            shadowElevation = 10.dp,
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(
                    listOf(stop.dominantColor.copy(0.35f), stop.accentColor.copy(0.50f))
                )
            ),
            modifier = Modifier.fillMaxWidth(0.82f)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                // "LIA dice:" row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(stop.accentColor)
                    )
                    Text(
                        "LIA dice:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = stop.dominantColor.copy(alpha = 0.75f)
                    )
                }
                Spacer(Modifier.height(7.dp))
                Text(
                    text = shownText,
                    fontSize = 14.sp,
                    lineHeight = 21.sp,
                    color = Color(0xFF2D3748),
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Ambient decorative blobs
// ─────────────────────────────────────────────────────────────

@Composable
private fun AmbientBlobs(primary: Color, accent: Color) {
    val inf = rememberInfiniteTransition(label = "blob")
    val t by inf.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = LinearEasing), RepeatMode.Reverse),
        label = "bt"
    )
    Canvas(Modifier.fillMaxSize()) {
        drawCircle(primary.copy(0.07f), size.width * 0.38f, Offset(size.width * 0.12f, size.height * 0.08f + t * 28f))
        drawCircle(accent.copy(0.06f), size.width * 0.32f, Offset(size.width * 0.88f, size.height * 0.88f - t * 22f))
        drawCircle(primary.copy(0.04f), size.width * 0.22f, Offset(size.width * 0.50f, size.height * 0.52f))
    }
}

// ─────────────────────────────────────────────────────────────
//  Draw helper
// ─────────────────────────────────────────────────────────────

private fun DrawScope.drawGlowRing(color: Color, alpha: Float) {
    val c = Offset(size.width / 2f, size.height / 2f)
    val r = size.minDimension / 2f
    drawCircle(color.copy(alpha = alpha * 0.50f), r, c)
    drawCircle(color.copy(alpha = alpha * 0.28f), r * 0.76f, c)
}
