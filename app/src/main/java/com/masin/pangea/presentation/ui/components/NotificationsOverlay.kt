package com.masin.pangea.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masin.pangea.ui.theme.PrimaryRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Colores del overlay de notificaciones
private val HeaderRed = Color(0xFF40FFD4)
private val NotificationBackground = Color(0xFFF8F9FA)
private val UnreadIndicator = Color(0xFFFFB300)  // Amarillo para indicar no leído
private val TextGray = Color(0xFF424242)
private val TextLightGray = Color(0xFF757575)
private val CardBackground = Color.White

/**
 * Modelo de notificación de ejemplo para tickets de Zoho Desk.
 * Preparado para integración posterior con datos reales.
 */
data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val ticketId: String?,
    val timestamp: Long,
    val type: NotificationType,
    val isRead: Boolean = false
)

enum class NotificationType(val icon: ImageVector) {
    TICKET_UPDATED(Icons.Default.Assignment),
    NEW_COMMENT(Icons.Default.Comment),
    STATUS_CHANGED(Icons.Default.CheckCircle),
    ASSIGNED(Icons.Default.Person)
}

/**
 * Overlay de notificaciones con ejemplos de Zoho Desk.
 * Por ahora solo la UI; la integración con datos reales será posterior.
 */
private val HeaderHeight = 56.dp
private val PeakWidth = 20.dp
private val PeakHeight = 15.dp

@Composable
fun NotificationsOverlay(
    onDismiss: () -> Unit
) {
    val density = LocalDensity.current
    val statusBarTop = WindowInsets.statusBars.getTop(density)
    val statusBarTopDp: Dp = density.run { statusBarTop.toDp() }
    val panelTopOffset = statusBarTopDp + HeaderHeight

    val notifications = remember {
        listOf(
            NotificationItem(
                id = "1",
                title = "Ticket actualizado",
                body = "El ticket #SDH-4521 ha sido actualizado. Revisa los cambios en el portal de casos.",
                ticketId = "SDH-4521",
                timestamp = System.currentTimeMillis() - 15 * 60 * 1000,  // Hace 15 min
                type = NotificationType.TICKET_UPDATED,
                isRead = false
            ),
            NotificationItem(
                id = "2",
                title = "Nuevo comentario",
                body = "Se agregó un comentario en el ticket #SDH-4489 sobre tu solicitud de devolución.",
                ticketId = "SDH-4489",
                timestamp = System.currentTimeMillis() - 2 * 60 * 60 * 1000,  // Hace 2 horas
                type = NotificationType.NEW_COMMENT,
                isRead = false
            ),
            NotificationItem(
                id = "3",
                title = "Estado cambiado",
                body = "El ticket #SDH-4401 pasó de 'En proceso' a 'Resuelto'.",
                ticketId = "SDH-4401",
                timestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000,  // Hace 1 día
                type = NotificationType.STATUS_CHANGED,
                isRead = true
            ),
            NotificationItem(
                id = "4",
                title = "Ticket asignado",
                body = "Se te asignó el ticket #SDH-4530 para seguimiento de tu declaración complementaria.",
                ticketId = "SDH-4530",
                timestamp = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000,  // Hace 3 días
                type = NotificationType.ASSIGNED,
                isRead = true
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .width(320.dp)
                .height(540.dp + PeakHeight)
                .padding(top = panelTopOffset - PeakHeight, end = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { /* Evita cerrar al tocar dentro del panel */ }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = PeakHeight)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(NotificationBackground)
            ) {
                // Header (sin botón X - se cierra tocando fuera)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(HeaderRed)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Notificaciones",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

            // Lista de notificaciones (todas en scroll, visibles de a 3)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(max = 500.dp)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(notification = notification)
                }
            }
            }

            // Pico triangular saliendo desde la campana, color rojo del header
            Canvas(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 0.dp, end = 8.dp)
                    .size(PeakWidth, PeakHeight)
            ) {
                val path = Path().apply {
                    moveTo(size.width / 2f, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(path, HeaderRed)
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationItem) {
    val timeAgo = formatTimeAgo(notification.timestamp)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Indicador de no leído + icono
        Box {
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(8.dp)
                        .background(UnreadIndicator, CircleShape)
                )
            }
            Icon(
                imageVector = notification.type.icon,
                contentDescription = null,
                tint = if (notification.isRead) TextLightGray else PrimaryRed,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                fontSize = 14.sp,
                fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                color = Color.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.body,
                fontSize = 12.sp,
                color = TextGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = timeAgo,
                fontSize = 11.sp,
                color = TextLightGray
            )
        }
    }
}

private fun formatTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60 * 1000 -> "Hace un momento"
        diff < 60 * 60 * 1000 -> "Hace ${diff / (60 * 1000)} min"
        diff < 24 * 60 * 60 * 1000 -> "Hace ${diff / (60 * 60 * 1000)} h"
        diff < 7 * 24 * 60 * 60 * 1000 -> "Hace ${diff / (24 * 60 * 60 * 1000)} días"
        else -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(timestamp))
    }
}
