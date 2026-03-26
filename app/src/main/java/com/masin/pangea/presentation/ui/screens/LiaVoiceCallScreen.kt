package com.masin.pangea.presentation.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masin.pangea.R
import com.masin.pangea.data.config.PangeaTtsVoices
import androidx.core.content.ContextCompat
import com.masin.pangea.data.remote.ChatMessage
import com.masin.pangea.data.remote.LiaApiResponse
import com.masin.pangea.data.remote.LiaApiService
import com.masin.pangea.data.remote.ServerStatus
import com.masin.pangea.presentation.ui.utils.rememberAppDimens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// private const val INITIAL_MESSAGE = "Hola, soy MASINA, asistente virtual de Grupo Masin. ¿En qué puedo ayudarte hoy?"
private const val INITIAL_MESSAGE = "¿ En que puedo ayudarte hoy ?"
private val HeaderCyan = Color(0xFF006CBF)
private val ChatBackground = Color(0xFFF8F9FA)
private val UserBubbleColor = Color(0xFF0D5C5C)
private val AssistantBubbleColor = Color.White


@Composable
fun LiaVoiceCallScreen(onBackPressed: () -> Unit = {}) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var textInput by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var serverStatus by remember { mutableStateOf<ServerStatus>(ServerStatus.Checking) }
    var isListening by remember { mutableStateOf(false) }
    var isSpeaking by remember { mutableStateOf(false) }
    var interimTranscript by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showWelcome by remember { mutableStateOf(true) }
    var hasRecordPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val apiService = remember { LiaApiService() }
    val listState = rememberLazyListState()
    var tts by remember { mutableStateOf<TextToSpeech?>(null) }
    var ttsReady by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasRecordPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "Se requiere permiso de micrófono para usar esta función", Toast.LENGTH_LONG).show()
        }
    }

    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    DisposableEffect(Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.let { engine -> PangeaTtsVoices.apply(engine, PangeaTtsVoices.liaChat) }
                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) { isSpeaking = true }
                    override fun onDone(utteranceId: String?) { isSpeaking = false }
                    override fun onError(utteranceId: String?) { isSpeaking = false }
                })
                ttsReady = true
            }
        }
        onDispose { tts?.stop(); tts?.shutdown() }
    }

    DisposableEffect(Unit) {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) { isListening = true; interimTranscript = "Escuchando..." }
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() { isListening = false; interimTranscript = "" }
                    override fun onError(error: Int) {
                        isListening = false; interimTranscript = ""
                        val msg = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "No se detectó ningún habla"
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Tiempo de espera agotado"
                            SpeechRecognizer.ERROR_AUDIO -> "Error de audio"
                            SpeechRecognizer.ERROR_NETWORK -> "Error de red"
                            else -> "Error de reconocimiento"
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                    override fun onResults(results: Bundle?) {
                        isListening = false; interimTranscript = ""
                        val transcript = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()
                        if (!transcript.isNullOrEmpty()) {
                            coroutineScope.launch {
                                handleUserMessage(transcript, messages, { messages = it }, { isLoading = it }, { errorMessage = it }, apiService, tts) { serverStatus = it }
                            }
                        }
                    }
                    override fun onPartialResults(partialResults: Bundle?) {
                        interimTranscript = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull() ?: "Escuchando..."
                    }
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
        onDispose { speechRecognizer?.destroy() }
    }

    LaunchedEffect(Unit) { serverStatus = apiService.checkStatus() }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) { delay(100); listState.animateScrollToItem(messages.size - 1) }
    }
    
    LaunchedEffect(textInput) {
        if (textInput.isNotEmpty() && messages.isNotEmpty()) {
            delay(50); listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun startListening() {
        if (!hasRecordPermission) { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO); return }
        tts?.stop(); isSpeaking = false
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() { speechRecognizer?.stopListening(); isListening = false; interimTranscript = "" }
    fun stopSpeaking() { tts?.stop(); isSpeaking = false }

    fun sendTextMessage() {
        val text = textInput.trim()
        if (text.isEmpty() || isLoading) return
        textInput = ""; focusManager.clearFocus()
        coroutineScope.launch {
            handleUserMessage(text, messages, { messages = it }, { isLoading = it }, { errorMessage = it }, apiService, tts) { serverStatus = it }
        }
    }

    LaunchedEffect(ttsReady) {
        if (showWelcome && ttsReady && tts != null) {
            showWelcome = false
            stopSpeaking()
            val assistantMessage = ChatMessage(role = "assistant", content = INITIAL_MESSAGE)
            messages = listOf(assistantMessage)
            tts?.speak(INITIAL_MESSAGE, TextToSpeech.QUEUE_FLUSH, null, "welcome")
        }
    }

    val dimens = rememberAppDimens()
    // Ancho máximo de burbuja: 70% del ancho en compact, 55% en tablet
    val bubbleMaxWidthFraction = if (dimens.isTablet) 0.55f else 0.70f

    Column(modifier = Modifier.fillMaxSize().background(ChatBackground)) {
        BackHandler(enabled = true) {
            stopSpeaking()
            onBackPressed()
        }

        LiaChatHeader(
            serverStatus = serverStatus,
            dimens = dimens,
            onClearChat = {
                stopSpeaking()
                errorMessage = null
                val assistantMessage = ChatMessage(role = "assistant", content = INITIAL_MESSAGE)
                messages = listOf(assistantMessage)
                tts?.speak(INITIAL_MESSAGE, TextToSpeech.QUEUE_FLUSH, null, "welcome")
            }
        )

        AnimatedVisibility(visible = errorMessage != null) {
            LiaErrorBanner(message = errorMessage ?: "", onDismiss = { errorMessage = null })
        }

        AnimatedVisibility(visible = isSpeaking) {
            LiaSpeakingIndicator(onStop = { stopSpeaking() })
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = dimens.paddingScreen, vertical = dimens.spacingSmall),
                verticalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
            ) {
                items(messages) { message ->
                    LiaChatBubble(
                        message = message,
                        bubbleMaxWidthFraction = bubbleMaxWidthFraction,
                        dimens = dimens
                    )
                }
                if (isLoading) { item { LiaTypingIndicator(dimens = dimens) } }
            }
        }

        LiaInputFooter(
            textInput = textInput,
            onTextChange = { textInput = it },
            onSendClick = { sendTextMessage() },
            isLoading = isLoading,
            isListening = isListening,
            isSpeaking = isSpeaking,
            interimTranscript = interimTranscript,
            onMicClick = { if (isListening) stopListening() else startListening() },
            dimens = dimens
        )
    }
}

@Composable
private fun LiaChatHeader(
    serverStatus: ServerStatus,
    onClearChat: () -> Unit,
    dimens: com.masin.pangea.presentation.ui.utils.AppDimens
) {
    Surface(modifier = Modifier.fillMaxWidth(), color = HeaderCyan, shadowElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.paddingScreen, vertical = dimens.spacingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lia_avatar),
                    contentDescription = "LIA",
                    modifier = Modifier.size(dimens.avatarSize).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column {
                    Text("LIA", color = Color.White, fontSize = dimens.fontSubtitle, fontWeight = FontWeight.Bold)
                    Text("Asistente de voz", color = Color.White.copy(alpha = 0.8f), fontSize = dimens.fontCaption)
                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimens.spacingSmall),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LiaStatusIndicator(status = serverStatus, dimens = dimens)
                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier
                        .size(dimens.iconSizeLarge)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Limpiar chat",
                        tint = Color.White,
                        modifier = Modifier.size(dimens.iconSizeMedium)
                    )
                }
            }
        }
    }
}

@Composable
private fun LiaStatusIndicator(
    status: ServerStatus,
    dimens: com.masin.pangea.presentation.ui.utils.AppDimens
) {
    val statusColor = when (status) {
        is ServerStatus.Connected -> Color(0xFF6BCB77)
        is ServerStatus.Disconnected -> Color(0xFFFF6B6B)
        is ServerStatus.Checking -> Color(0xFFFFD93D)
    }
    val statusText = when (status) {
        is ServerStatus.Connected -> "Conectado"
        is ServerStatus.Disconnected -> "Desconectado"
        is ServerStatus.Checking -> "Verificando..."
    }
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.5f,
        animationSpec = infiniteRepeatable(animation = tween(1000), repeatMode = RepeatMode.Reverse),
        label = "pulse_alpha"
    )

    Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(20.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = dimens.spacingMedium, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        statusColor.copy(alpha = if (status is ServerStatus.Checking) alpha else 1f),
                        CircleShape
                    )
            )
            Text(text = statusText, color = Color.White, fontSize = dimens.fontCaption)
        }
    }
}

@Composable
private fun LiaErrorBanner(message: String, onDismiss: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), color = Color(0xFFFFF3CD)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⚠️ $message", color = Color(0xFF856404), fontSize = 14.sp, modifier = Modifier.weight(1f))
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Text("✕", fontSize = 14.sp, color = Color(0xFF856404))
            }
        }
    }
}

@Composable
private fun LiaSpeakingIndicator(onStop: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().background(
            brush = Brush.horizontalGradient(colors = listOf(Color(0xFF11998E), Color(0xFF38EF7D)))
        ).padding(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            val infiniteTransition = rememberInfiniteTransition(label = "speaker")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f, targetValue = 1.2f,
                animationSpec = infiniteRepeatable(animation = tween(500), repeatMode = RepeatMode.Reverse),
                label = "speaker_scale"
            )
            Text("🔊", fontSize = 20.sp, modifier = Modifier.scale(scale))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Hablando...", color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(12.dp))
            TextButton(onClick = onStop, colors = ButtonDefaults.textButtonColors(contentColor = Color.White)) {
                Text("Detener", fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun LiaChatBubble(
    message: ChatMessage,
    bubbleMaxWidthFraction: Float = 0.70f,
    dimens: com.masin.pangea.presentation.ui.utils.AppDimens
) {
    val isUser = message.role == "user"
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val avatarSize = dimens.avatarSize

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Image(
                painter = painterResource(id = R.drawable.lia_avatar),
                contentDescription = "LIA",
                modifier = Modifier.size(avatarSize).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(dimens.spacingSmall))
        }
        // Ancho máximo dinámico: fracción del ancho total de la pantalla
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(bubbleMaxWidthFraction)
        ) {
            Surface(
                color = if (isUser) UserBubbleColor else AssistantBubbleColor,
                shape = RoundedCornerShape(
                    topStart = 18.dp, topEnd = 18.dp,
                    bottomStart = if (isUser) 18.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 18.dp
                ),
                shadowElevation = if (isUser) 0.dp else 2.dp
            ) {
                Text(
                    text = message.content,
                    color = if (isUser) Color.White else Color(0xFF333333),
                    fontSize = dimens.fontBody,
                    lineHeight = dimens.lineHeightBody,
                    modifier = Modifier.padding(horizontal = dimens.spacingMedium, vertical = dimens.spacingSmall + 2.dp)
                )
            }
            Text(
                text = timeFormat.format(Date(message.timestamp)),
                color = Color(0xFF999999),
                fontSize = dimens.fontSmall,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp)
            )
        }
        if (isUser) {
            Spacer(modifier = Modifier.width(dimens.spacingSmall))
            Surface(
                modifier = Modifier.size(avatarSize),
                shape = CircleShape,
                color = HeaderCyan
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("👤", fontSize = dimens.fontSubtitle)
                }
            }
        }
    }
}

@Composable
private fun LiaTypingIndicator(
    dimens: com.masin.pangea.presentation.ui.utils.AppDimens
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
        Image(
            painter = painterResource(id = R.drawable.lia_avatar),
            contentDescription = "LIA",
            modifier = Modifier.size(dimens.avatarSize).clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(dimens.spacingSmall))
        Surface(
            color = AssistantBubbleColor,
            shape = RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = dimens.paddingCard, vertical = dimens.spacingMedium),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    val infiniteTransition = rememberInfiniteTransition(label = "typing_$index")
                    val offsetY by infiniteTransition.animateFloat(
                        initialValue = 0f, targetValue = -8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(400, delayMillis = index * 150),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_$index"
                    )
                    Box(modifier = Modifier.size(8.dp).offset(y = offsetY.dp).background(HeaderCyan, CircleShape))
                }
            }
        }
    }
}

@Composable
private fun LiaInputFooter(
    textInput: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean,
    isListening: Boolean,
    isSpeaking: Boolean,
    interimTranscript: String,
    onMicClick: () -> Unit,
    dimens: com.masin.pangea.presentation.ui.utils.AppDimens
) {
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val isImeVisible = imeBottom > 0
    val micSize = if (dimens.isTablet) 96.dp else 80.dp

    Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 8.dp) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimens.paddingScreen)
                .padding(top = dimens.spacingSmall, bottom = if (isImeVisible) dimens.spacingMedium else dimens.spacingXLarge + 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.spacingSmall)
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = onTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Escribe aquí...", color = Color.Gray, fontSize = dimens.fontBody) },
                    enabled = !isLoading && !isListening,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HeaderCyan,
                        unfocusedBorderColor = Color(0xFFE6E6E6)
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSendClick() })
                )
                FilledIconButton(
                    onClick = onSendClick,
                    enabled = textInput.isNotBlank() && !isLoading,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = HeaderCyan,
                        disabledContainerColor = HeaderCyan.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.size(dimens.buttonHeight)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = Color.White,
                        modifier = Modifier.size(dimens.iconSizeMedium)
                    )
                }
            }

            if (!isImeVisible) {
                Spacer(modifier = Modifier.height(dimens.spacingMedium))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedVisibility(visible = isListening && interimTranscript.isNotEmpty()) {
                        Surface(
                            color = HeaderCyan.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(bottom = dimens.spacingSmall)
                        ) {
                            Text(
                                text = interimTranscript,
                                color = HeaderCyan,
                                fontStyle = FontStyle.Italic,
                                fontSize = dimens.fontBody,
                                modifier = Modifier.padding(horizontal = dimens.paddingCard, vertical = dimens.spacingSmall)
                            )
                        }
                    }
                    LiaMicrophoneButton(
                        isListening = isListening,
                        isDisabled = isLoading || isSpeaking,
                        onClick = onMicClick,
                        size = micSize
                    )
                    Spacer(modifier = Modifier.height(dimens.spacingSmall))
                    Text(
                        text = when {
                            isListening -> "Hablando... Toca para detener"
                            isLoading -> "Esperando respuesta..."
                            else -> "Toca el micrófono para hablar"
                        },
                        color = Color(0xFF888888),
                        fontSize = dimens.fontCaption
                    )
                }
            }
        }
    }
}

@Composable
private fun LiaMicrophoneButton(
    isListening: Boolean,
    isDisabled: Boolean,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 80.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = if (isListening) 1.1f else 1f,
        animationSpec = infiniteRepeatable(animation = tween(700), repeatMode = RepeatMode.Reverse),
        label = "mic_scale"
    )
    val backgroundModifier = when {
        isDisabled -> Modifier.background(Color(0xFFCCCCCC), CircleShape)
        isListening -> Modifier.background(
            brush = Brush.linearGradient(listOf(Color(0xFFF5576C), Color(0xFFF093FB))),
            shape = CircleShape
        )
        else -> Modifier.background(HeaderCyan, CircleShape)
    }
    val iconSize = size * 0.4f

    Box(
        modifier = Modifier
            .size(size)
            .scale(if (isListening) scale else 1f)
            .shadow(
                elevation = if (isListening) 12.dp else 6.dp,
                shape = CircleShape,
                ambientColor = if (isListening) Color(0xFFF5576C) else HeaderCyan
            )
            .then(backgroundModifier)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick, enabled = !isDisabled, modifier = Modifier.fillMaxSize()) {
            if (isListening) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Detener",
                    tint = Color.White,
                    modifier = Modifier.size(iconSize)
                )
            } else {
                Text("🎤", fontSize = (size.value * 0.35f).sp)
            }
        }
    }
}

private suspend fun handleUserMessage(
    text: String,
    messages: List<ChatMessage>,
    onMessagesUpdate: (List<ChatMessage>) -> Unit,
    onLoadingChange: (Boolean) -> Unit,
    onError: (String?) -> Unit,
    apiService: LiaApiService,
    tts: TextToSpeech?,
    onServerStatusChange: (ServerStatus) -> Unit
) {
    tts?.stop()
    val userMessage = ChatMessage(role = "user", content = text)
    val updatedMessages = messages + userMessage
    onMessagesUpdate(updatedMessages)
    onLoadingChange(true)
    onError(null)
    try {
        when (val response = apiService.sendMessage(text, messages)) {
            is LiaApiResponse.Success -> {
                val assistantMessage = ChatMessage(role = "assistant", content = response.message)
                onMessagesUpdate(updatedMessages + assistantMessage)
                onServerStatusChange(ServerStatus.Connected)
                tts?.speak(response.message, TextToSpeech.QUEUE_FLUSH, null, "response")
            }
            is LiaApiResponse.Error -> {
                val errorMsg = ChatMessage(role = "assistant", content = "Lo siento, hubo un problema al procesar tu mensaje. Por favor, intenta de nuevo.")
                onMessagesUpdate(updatedMessages + errorMsg)
                onError(response.error)
                tts?.speak("Lo siento, hubo un problema. Por favor, intenta de nuevo.", TextToSpeech.QUEUE_FLUSH, null, "error")
            }
        }
    } catch (e: Exception) {
        val errorMsg = ChatMessage(role = "assistant", content = "Lo siento, hubo un problema de conexión. Por favor, intenta de nuevo.")
        onMessagesUpdate(updatedMessages + errorMsg)
        onError(e.message)
    } finally {
        onLoadingChange(false)
    }
}
