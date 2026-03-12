package com.masin.pangea.presentation.navigation

import androidx.compose.ui.graphics.Color
import com.masin.pangea.R

/**
 * Rutas de navegación de la aplicación
 */
object NavRoutes {
    const val HOME = "home"
    const val LIA = "lia"
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
    data object Conoce : BottomNavItem(
        route = "conoce",
        title = "Conoce",
        iconResId = R.drawable.conoce,
        selectedColor = Color(0xFFFFB300)   // Amarillo
    )

    data object Gestiona : BottomNavItem(
        route = "gestiona",
        title = "Gestiona",
        iconResId = R.drawable.gestiona,
        selectedColor = Color(0xFF1565C0)   // Azul
    )

    data object Soluciona : BottomNavItem(
        route = "soluciona",
        title = "Soluciona",
        iconResId = R.drawable.soluciona,
        selectedColor = Color(0xFFE53935)   // Rojo
    )

    data object Paga : BottomNavItem(
        route = "paga",
        title = "Paga",
        iconResId = R.drawable.paga,
        selectedColor = Color(0xFF2E7D32)   // Verde
    )
    
    companion object {
        val items = listOf(Conoce, Gestiona, Soluciona, Paga)
    }
}
