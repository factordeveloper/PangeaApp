package com.masin.pangea.presentation.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.masin.pangea.R
import com.masin.pangea.ui.theme.PangeaGreen

private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.RECORD_AUDIO
)

@Composable
fun PermissionsHandler(
    onAllPermissionsGranted: @Composable () -> Unit
) {
    val context = LocalContext.current

    var allPermissionsGranted by remember {
        mutableStateOf(
            REQUIRED_PERMISSIONS.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }
        )
    }

    var showSettingsDialog by remember { mutableStateOf(false) }
    var permissionsRequested by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionsRequested = true
        allPermissionsGranted = permissions.values.all { it }
        if (!allPermissionsGranted) {
            showSettingsDialog = true
        }
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        allPermissionsGranted = REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    if (allPermissionsGranted) {
        onAllPermissionsGranted()
    } else {
        PermissionsScreen(
            onRequestPermissions = { permissionLauncher.launch(REQUIRED_PERMISSIONS) },
            onOpenSettings = {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                settingsLauncher.launch(intent)
            },
            showSettingsOption = permissionsRequested && !allPermissionsGranted
        )

        if (showSettingsDialog) {
            PermissionsDeniedDialog(
                onDismiss = { showSettingsDialog = false },
                onOpenSettings = {
                    showSettingsDialog = false
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    settingsLauncher.launch(intent)
                }
            )
        }
    }
}

@Composable
private fun PermissionsScreen(
    onRequestPermissions: () -> Unit,
    onOpenSettings: () -> Unit,
    showSettingsOption: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF40EBFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = PangeaGreen,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lia_avatar),
                        contentDescription = "LIA",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "¡Bienvenido a PANGEA!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Para brindarte la mejor experiencia, necesitamos tu permiso para acceder a algunas funciones de tu dispositivo.",
                fontSize = 16.sp,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            PermissionCardWithEmoji(
                emoji = "🎤",
                title = "Micrófono",
                description = "Para hablar con LIA usando comandos de voz y realizar llamadas de voz."
            )

            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = onRequestPermissions,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PangeaGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Permitir acceso",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF000000)
                )
            }

            if (showSettingsOption) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Abrir configuración",
                        fontSize = 16.sp,
                        color = PangeaGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tus datos están protegidos y solo se usan para mejorar tu experiencia en la app.",
                fontSize = 12.sp,
                color = Color(0xFF000000),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PermissionCardWithEmoji(
    emoji: String,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = PangeaGreen.copy(alpha = 0.1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 24.sp)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333)
                )
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
private fun PermissionsDeniedDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Permisos necesarios", fontWeight = FontWeight.Bold) },
        text = {
            Text(
                text = "Para usar el chat de voz con MASINA, necesitas otorgar el permiso de micrófono.\n\nPuedes habilitarlo desde la configuración de la aplicación.",
                lineHeight = 22.sp
            )
        },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text(text = "Ir a configuración", color = PangeaGreen, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Más tarde", color = Color.Gray)
            }
        }
    )
}
