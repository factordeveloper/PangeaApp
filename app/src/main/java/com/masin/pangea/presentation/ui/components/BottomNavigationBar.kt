package com.masin.pangea.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.masin.pangea.presentation.navigation.BottomNavItem
import com.masin.pangea.presentation.navigation.NavRoutes

// Colores personalizados para la barra de navegación
private val NavBarBackground = Color(0xFF3EFFD4)
private val NavBarIconColor = Color(0xFF0D00BF)
private val NavBarSelectedColor = Color.White

/**
 * Componente de barra de navegación inferior personalizada
 */
@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar(
        modifier = Modifier.shadow(
            elevation = 12.dp,
            shape = RectangleShape,
            clip = true
        ),
        containerColor = NavBarBackground,
        contentColor = NavBarIconColor
    ) {
        BottomNavItem.items.forEach { item ->
            val isSelected = currentRoute == item.route
            val iconTint = if (isSelected) NavBarSelectedColor else NavBarIconColor
            val textColor = if (isSelected) NavBarSelectedColor else NavBarIconColor
            
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp),
                        tint = iconTint
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = textColor,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop hasta home para limpiar el back stack al cambiar de pestaña
                            popUpTo(NavRoutes.HOME) {
                                saveState = true
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = iconTint,
                    unselectedIconColor = NavBarIconColor,
                    selectedTextColor = textColor,
                    unselectedTextColor = NavBarIconColor,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
