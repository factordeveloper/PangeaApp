package com.masin.pangea.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.masin.pangea.R
import com.masin.pangea.presentation.ui.utils.rememberAppDimens
import com.masin.pangea.ui.theme.PANGEAappTheme

// ── Paleta de colores ──────────────────────────────────────────────────────────
private val DeskBackgroundDark  = Color.White
private val DeskBackgroundTeal  = Color(0xFF1A3A3A)
private val CardTeal            = Color(0xFF0D5C5C)
private val CardTealAlt         = Color(0xFF0A4A4A)
private val ButtonDark          = Color(0xFF1A2E2E)
private val TextWhite           = Color.White
private val AccentGreen         = Color(0xFF4CAF88)
private val AccentAmber         = Color(0xFFFFA726)
private val AccentRed           = Color(0xFFEF5350)
private val SurfaceLight        = Color(0xFFF0F4F4)
private val TextDark            = Color(0xFF1A2E2E)

// ── Modelo de datos ────────────────────────────────────────────────────────────
enum class TicketStatus(val label: String, val color: Color) {
    OPEN("Abierto", AccentGreen),
    IN_PROGRESS("En progreso", AccentAmber),
    CLOSED("Cerrado", AccentRed)
}

data class Ticket(
    val id: Int,
    val title: String,
    val description: String,
    val status: TicketStatus,
    val category: String,
    val date: String
)

private fun sampleTickets() = listOf(
    Ticket(1, "Error en facturación", "El sistema no genera la factura al finalizar el pago.", TicketStatus.OPEN, "Pagos", "24/03/2026"),
    Ticket(2, "Acceso denegado al módulo", "No puedo acceder al módulo de reportes con mi rol.", TicketStatus.IN_PROGRESS, "Accesos", "25/03/2026"),
    Ticket(3, "Actualización de datos", "Cambio de dirección de correspondencia.", TicketStatus.CLOSED, "Datos", "26/03/2026"),
)

// ── Pantalla principal ─────────────────────────────────────────────────────────
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun DeskScreen() {
    val dimens = rememberAppDimens()
    val scrollState = rememberScrollState()

    // Estados de flujo
    var showAuthModal   by remember { mutableStateOf(false) }
    var isLoggedIn      by remember { mutableStateOf(false) }

    // Estado CRUD
    var tickets         by remember { mutableStateOf(sampleTickets()) }
    var nextId          by remember { mutableStateOf(tickets.size + 1) }
    var showCreateDialog  by remember { mutableStateOf(false) }
    var ticketToEdit    by remember { mutableStateOf<Ticket?>(null) }
    var ticketToDelete  by remember { mutableStateOf<Ticket?>(null) }
    var filterStatus    by remember { mutableStateOf<TicketStatus?>(null) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val contentMaxWidth  = dimens.maxContentWidth
        val horizontalPad    = ((maxWidth - contentMaxWidth) / 2).coerceAtLeast(0.dp)

        if (!isLoggedIn) {
            // ════════════════════════════════════════════════════════
            //  VISTA PÚBLICA — scroll con header + tarjetas
            // ════════════════════════════════════════════════════════
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(DeskBackgroundTeal, DeskBackgroundDark)))
                        .verticalScroll(scrollState)
                ) {
                    // Header
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimens.paddingSection + horizontalPad, vertical = dimens.spacingLarge),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("DESK", fontSize = dimens.fontHero, fontWeight = FontWeight.Bold, color = TextWhite)
                        Spacer(Modifier.height(dimens.spacingMedium))
                        Text(
                            text = "En este espacio encontrarás toda la información que necesitas para cumplir con tus obligaciones tributarias.",
                            fontSize = dimens.fontBody,
                            color = TextWhite,
                            lineHeight = dimens.lineHeightBody,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Fila: Login card + Branding card
                    val cardHeight = if (dimens.isTablet)
                        (dimens.screenHeightDp * 0.25f).dp.coerceIn(180.dp, 260.dp)
                    else
                        (180f * dimens.scaleFactor).dp.coerceIn(160.dp, 220.dp)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = dimens.paddingScreen + horizontalPad),
                        horizontalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
                    ) {
                        // Tarjeta Login
                        Card(
                            modifier = Modifier.weight(1f).height(cardHeight).clip(RoundedCornerShape(dimens.cardCornerRadius * 1.5f)),
                            shape = RoundedCornerShape(dimens.cardCornerRadius * 1.5f),
                            colors = CardDefaults.cardColors(containerColor = CardTeal),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(dimens.paddingCard),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(
                                    onClick = { showAuthModal = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(dimens.buttonCornerRadius),
                                    colors = ButtonDefaults.buttonColors(containerColor = ButtonDark)
                                ) { Text("Iniciar sesión", color = TextWhite, fontSize = dimens.fontCaption) }
                                Spacer(Modifier.height(dimens.spacingMedium))
                                Text("¿No tienes una cuenta?", fontSize = dimens.fontCaption, color = TextWhite)
                                Spacer(Modifier.height(dimens.spacingSmall))
                                Button(
                                    onClick = { /* TODO */ },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(dimens.buttonCornerRadius),
                                    colors = ButtonDefaults.buttonColors(containerColor = ButtonDark)
                                ) { Text("Crear cuenta", color = TextWhite, fontSize = dimens.fontCaption) }
                            }
                        }

                        // Tarjeta branding
                        Card(
                            modifier = Modifier.weight(1f).height(cardHeight).clip(RoundedCornerShape(dimens.cardCornerRadius * 1.5f)),
                            shape = RoundedCornerShape(dimens.cardCornerRadius * 1.5f),
                            colors = CardDefaults.cardColors(containerColor = CardTeal),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Image(painterResource(R.drawable.fondo_texto), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))
                                Column(Modifier.fillMaxSize().padding(dimens.paddingCard), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(painterResource(R.drawable.pangea), "Pangea", Modifier.size(dimens.logoSize), contentScale = ContentScale.Fit)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(dimens.spacingLarge))

                    // Tarjetas de acción
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = dimens.paddingScreen + horizontalPad),
                        verticalArrangement = Arrangement.spacedBy(dimens.spacingMedium)
                    ) {
                        DeskActionCard("MIS CASOS",   "Consulta y gestiona tus casos activos", onClick = { showAuthModal = true }, dimens = dimens)
                        DeskActionCard("CREAR CASO",  "Abre un nuevo caso de soporte",          onClick = { showAuthModal = true }, dimens = dimens)
                    }

                    Spacer(Modifier.height(dimens.bottomBarSpacing))
                }

                // Modal de autenticación
                if (showAuthModal) {
                    DeskAuthModal(
                        onDismiss  = { showAuthModal = false },
                        onLogin    = { isLoggedIn = true; showAuthModal = false },
                        onRegister = { showAuthModal = false }
                    )
                }
            }
        } else {
            // ════════════════════════════════════════════════════════
            //  VISTA AUTENTICADA — Dashboard CRUD de Tickets
            // ════════════════════════════════════════════════════════
            val filtered = if (filterStatus == null) tickets else tickets.filter { it.status == filterStatus }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SurfaceLight)
            ) {
                // Top bar del dashboard
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(DeskBackgroundTeal, Color(0xFF0D5C5C))))
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Column(modifier = Modifier.align(Alignment.CenterStart)) {
                        Text("Mesa de ayuda", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextWhite)
                        Text("Gestión de tickets", fontSize = 12.sp, color = TextWhite.copy(alpha = 0.75f))
                    }
                    Row(modifier = Modifier.align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
                        // Avatar simulado
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(AccentGreen),
                            contentAlignment = Alignment.Center
                        ) { Text("U", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = { isLoggedIn = false }) {
                            Icon(Icons.Default.ExitToApp, "Cerrar sesión", tint = TextWhite)
                        }
                    }
                }

                // Estadísticas rápidas
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatChip("Total",       tickets.size.toString(),                              Color(0xFF1A3A3A), Modifier.weight(1f))
                    StatChip("Abiertos",    tickets.count { it.status == TicketStatus.OPEN }.toString(),        AccentGreen, Modifier.weight(1f))
                    StatChip("En curso",    tickets.count { it.status == TicketStatus.IN_PROGRESS }.toString(), AccentAmber, Modifier.weight(1f))
                    StatChip("Cerrados",    tickets.count { it.status == TicketStatus.CLOSED }.toString(),      AccentRed,   Modifier.weight(1f))
                }

                // Filtros de estado
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(selected = filterStatus == null, onClick = { filterStatus = null },  label = { Text("Todos") })
                    TicketStatus.entries.forEach { status ->
                        FilterChip(
                            selected = filterStatus == status,
                            onClick  = { filterStatus = if (filterStatus == status) null else status },
                            label    = { Text(status.label, fontSize = 11.sp) },
                            leadingIcon = if (filterStatus == status) {
                                { Icon(Icons.Default.Check, null, Modifier.size(14.dp)) }
                            } else null
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Lista de tickets
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    if (filtered.isEmpty()) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                                Text("No hay tickets", color = Color.Gray, fontSize = 15.sp)
                            }
                        }
                    }
                    items(filtered, key = { it.id }) { ticket ->
                        AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
                            TicketCard(
                                ticket    = ticket,
                                onEdit    = { ticketToEdit   = ticket },
                                onDelete  = { ticketToDelete = ticket }
                            )
                        }
                    }
                }

                // FAB crear ticket
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    ExtendedFloatingActionButton(
                        onClick           = { showCreateDialog = true },
                        icon              = { Icon(Icons.Default.Add, "Crear ticket") },
                        text              = { Text("Nuevo ticket") },
                        containerColor    = DeskBackgroundTeal,
                        contentColor      = TextWhite
                    )
                }
            }

            // ── Diálogo: Crear ticket ──────────────────────────────────────────
            if (showCreateDialog) {
                TicketDialog(
                    title       = "Crear ticket",
                    initial     = null,
                    onConfirm   = { t, d, s, c ->
                        tickets = tickets + Ticket(nextId++, t, d, s, c, "27/03/2026")
                        showCreateDialog = false
                    },
                    onDismiss   = { showCreateDialog = false }
                )
            }

            // ── Diálogo: Editar ticket ─────────────────────────────────────────
            ticketToEdit?.let { editing ->
                TicketDialog(
                    title       = "Editar ticket #${editing.id}",
                    initial     = editing,
                    onConfirm   = { t, d, s, c ->
                        tickets = tickets.map { if (it.id == editing.id) it.copy(title = t, description = d, status = s, category = c) else it }
                        ticketToEdit = null
                    },
                    onDismiss   = { ticketToEdit = null }
                )
            }

            // ── Diálogo: Confirmar eliminación ────────────────────────────────
            ticketToDelete?.let { del ->
                AlertDialog(
                    onDismissRequest = { ticketToDelete = null },
                    icon             = { Icon(Icons.Default.Warning, null, tint = AccentRed) },
                    title            = { Text("Eliminar ticket") },
                    text             = { Text("¿Deseas eliminar el ticket \"${del.title}\"? Esta acción no se puede deshacer.") },
                    confirmButton    = {
                        TextButton(onClick = {
                            tickets = tickets.filter { it.id != del.id }
                            ticketToDelete = null
                        }) { Text("Eliminar", color = AccentRed, fontWeight = FontWeight.Bold) }
                    },
                    dismissButton    = {
                        TextButton(onClick = { ticketToDelete = null }) { Text("Cancelar") }
                    }
                )
            }
        }
    }
}

// ── Tarjeta de ticket ──────────────────────────────────────────────────────────
@Composable
private fun TicketCard(ticket: Ticket, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // ID badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(DeskBackgroundTeal),
                    contentAlignment = Alignment.Center
                ) { Text("#${ticket.id}", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(ticket.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(ticket.category, fontSize = 11.sp, color = Color.Gray)
                }
                // Status chip
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(ticket.status.color.copy(alpha = 0.15f)).padding(horizontal = 10.dp, vertical = 4.dp)
                ) { Text(ticket.status.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ticket.status.color) }
            }

            Spacer(Modifier.height(8.dp))
            Text(ticket.description, fontSize = 12.sp, color = Color.DarkGray, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 17.sp)

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFE0E0E0))
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, Modifier.size(13.dp), tint = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text(ticket.date, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                // Botones editar / eliminar
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, "Editar", tint = DeskBackgroundTeal, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = AccentRed, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// ── Diálogo crear / editar ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TicketDialog(
    title:    String,
    initial:  Ticket?,
    onConfirm: (String, String, TicketStatus, String) -> Unit,
    onDismiss: () -> Unit
) {
    var ticketTitle  by remember { mutableStateOf(initial?.title       ?: "") }
    var description  by remember { mutableStateOf(initial?.description ?: "") }
    var category     by remember { mutableStateOf(initial?.category    ?: "") }
    var selectedStatus by remember { mutableStateOf(initial?.status    ?: TicketStatus.OPEN) }
    var statusExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold, color = TextDark) },
        text  = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value         = ticketTitle,
                    onValueChange = { ticketTitle = it },
                    label         = { Text("Título") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value         = description,
                    onValueChange = { description = it },
                    label         = { Text("Descripción") },
                    minLines      = 3,
                    maxLines      = 5,
                    modifier      = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value         = category,
                    onValueChange = { category = it },
                    label         = { Text("Categoría") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
                // Selector de estado
                ExposedDropdownMenuBox(
                    expanded         = statusExpanded,
                    onExpandedChange = { statusExpanded = !statusExpanded }
                ) {
                    OutlinedTextField(
                        value             = selectedStatus.label,
                        onValueChange     = {},
                        readOnly          = true,
                        label             = { Text("Estado") },
                        trailingIcon      = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                        modifier          = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded         = statusExpanded,
                        onDismissRequest = { statusExpanded = false }
                    ) {
                        TicketStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text    = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(status.color))
                                        Spacer(Modifier.width(8.dp))
                                        Text(status.label)
                                    }
                                },
                                onClick = { selectedStatus = status; statusExpanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick  = {
                    if (ticketTitle.isNotBlank() && description.isNotBlank())
                        onConfirm(ticketTitle.trim(), description.trim(), selectedStatus, category.trim().ifBlank { "General" })
                },
                colors   = ButtonDefaults.buttonColors(containerColor = DeskBackgroundTeal)
            ) { Text("Guardar", color = TextWhite) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// ── Modal de autenticación ─────────────────────────────────────────────────────
@Composable
private fun DeskAuthModal(
    onDismiss:  () -> Unit,
    onLogin:    () -> Unit,
    onRegister: () -> Unit
) {
    val dimens = rememberAppDimens()
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var hidePass by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .widthIn(max = if (dimens.isTablet) 480.dp else 400.dp)
                .fillMaxWidth(if (dimens.isTablet) 0.65f else 0.92f)
                .padding(horizontal = dimens.spacingLarge)
                .clickable { /* consumir clic */ },
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Box(modifier = Modifier.padding(dimens.spacingLarge)) {
                // Botón cerrar
                IconButton(
                    onClick  = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd).size(32.dp).background(ButtonDark, CircleShape)
                ) { Icon(Icons.Default.Close, "Cerrar", tint = TextWhite, modifier = Modifier.size(18.dp)) }

                Column(
                    modifier            = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Spacer(Modifier.height(4.dp))
                    // Logo
                    Image(
                        painterResource(R.drawable.pangea), "Pangea",
                        modifier     = Modifier.size(dimens.logoSize).clip(CircleShape),
                        contentScale = ContentScale.Fit
                    )
                    Text("Iniciar sesión", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextDark)
                    Text("Accede a tu cuenta de Mesa de Ayuda", fontSize = 12.sp, color = Color.Gray, textAlign = TextAlign.Center)

                    OutlinedTextField(
                        value         = email,
                        onValueChange = { email = it },
                        label         = { Text("Correo electrónico") },
                        leadingIcon   = { Icon(Icons.Default.Email, null) },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value            = password,
                        onValueChange    = { password = it },
                        label            = { Text("Contraseña") },
                        leadingIcon      = { Icon(Icons.Default.Lock, null) },
                        trailingIcon     = {
                            IconButton(onClick = { hidePass = !hidePass }) {
                                Icon(if (hidePass) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                            }
                        },
                        singleLine       = true,
                        modifier         = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick  = onLogin,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = DeskBackgroundTeal)
                    ) { Text("Iniciar sesión", color = TextWhite, fontWeight = FontWeight.Bold) }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("¿No tienes cuenta?", fontSize = 12.sp, color = Color.Gray)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Regístrate",
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color      = AccentGreen,
                            modifier   = Modifier.clickable(onClick = onRegister)
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
    }
}

// ── Chip de estadística ────────────────────────────────────────────────────────
@Composable
private fun StatChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(12.dp),
        colors    = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
            Text(label, fontSize = 10.sp, color = color.copy(alpha = 0.8f))
        }
    }
}

// ── Tarjeta de acción pública ──────────────────────────────────────────────────
@Composable
private fun DeskActionCard(
    title:    String,
    subtitle: String,
    onClick:  () -> Unit,
    dimens:   com.masin.pangea.presentation.ui.utils.AppDimens
) {
    Card(
        modifier  = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape     = RoundedCornerShape(dimens.cardCornerRadius * 1.5f),
        colors    = CardDefaults.cardColors(containerColor = CardTealAlt),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(dimens.paddingSection),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title,    fontSize = dimens.fontSubtitle, fontWeight = FontWeight.Bold, color = TextWhite)
            Spacer(Modifier.height(dimens.spacingSmall))
            Text(subtitle, fontSize = dimens.fontBody, color = TextWhite.copy(alpha = 0.9f), textAlign = TextAlign.Center)
        }
    }
}

// ── Preview ────────────────────────────────────────────────────────────────────
@Preview(showBackground = true)
@Composable
fun DeskScreenPreview() {
    PANGEAappTheme { DeskScreen() }
}
