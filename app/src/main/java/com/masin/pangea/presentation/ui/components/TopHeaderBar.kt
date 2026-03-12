package com.masin.pangea.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.masin.pangea.R
import com.masin.pangea.presentation.navigation.NavRoutes
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import com.masin.pangea.ui.theme.PangeaTeal
import com.masin.pangea.ui.theme.PrimaryRed

/**
 * Header superior con logo Pangea estilo CenterAlignedTopAppBar.
 * Incluye: menú hamburguesa, avatar de MASINA y campana de notificaciones.
 * Al tocar el avatar se abre directamente la UI de chat.
 * Al tocar la campana se despliega el panel de notificaciones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopHeaderBar(
    navController: NavController,
    onMenuClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Image(
                painter = painterResource(id = R.drawable.pangea),
                contentDescription = "Pangea Logo",
                modifier = Modifier
                    .width(250.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        navController.navigate(NavRoutes.HOME) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                contentScale = ContentScale.Fit
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu",
                    tint = PangeaTeal,
                    modifier = Modifier.size(35.dp)
                )
            }
        },
        actions = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar de MASINA - al tocar abre directamente la UI de chat
                Image(
                    painter = painterResource(id = R.drawable.lia_avatar),
                    contentDescription = "MASINA - Asistente virtual",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(2.dp, PrimaryRed, CircleShape)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            navController.navigate(NavRoutes.LIA) {
                                launchSingleTop = true
                            }
                        },
                    contentScale = ContentScale.Crop
                )
                // Campana de notificaciones - al tocar despliega el panel de notificaciones
                Image(
                    painter = painterResource(id = R.drawable.campana),
                    contentDescription = "Notificaciones",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onNotificationsClick() },
                    contentScale = ContentScale.Fit
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        )
    )
}

@Preview(showBackground = true)
@Composable
fun TopHeaderBarPreview() {
    TopHeaderBar(
        navController = rememberNavController(),
        onNotificationsClick = {}
    )
}
