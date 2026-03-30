package com.masin.pangea.presentation.navigation

import androidx.compose.ui.graphics.Color
import com.masin.pangea.R

/**
 * Rutas de navegación de la aplicación
 */
object NavRoutes {
    const val WALKTHROUGH = "walkthrough"
    const val WELCOME = "welcome"
    const val PLAN_SELECTION = "plan_selection"
    const val HOME = "home"
    const val LIA = "lia"
    const val RADAR_TERRITORIAL = "radar_territorial"
}

/**
 * Sealed class que define los items de navegación inferior
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val iconResId: Int,
    val selectedColor: Color
) {
    data object Pangea : BottomNavItem(
        route = NavRoutes.HOME,
        title = "Pangea",
        iconResId = R.drawable.conoce,
        selectedColor = Color(0xFF006CBF)   // Amarillo
    )

    data object ELearning : BottomNavItem(
        route = "elearning",
        title = "E - Learning",
        iconResId = R.drawable.gestiona,
        selectedColor = Color(0xFF00A9BF)   // Azul
    )

    data object Desk : BottomNavItem(
        route = "desk",
        title = "Desk",
        iconResId = R.drawable.soluciona,
        selectedColor = Color(0xFF40FF52)   // Rojo
    )

    data object Digiturno : BottomNavItem(
        route = "digiturno",
        title = "Digiturno",
        iconResId = R.drawable.paga,
        selectedColor = Color(0xFF40EBFF)   // Verde
    )
    
    companion object {
        val items = listOf(Pangea, ELearning, Desk, Digiturno)
    }
}
