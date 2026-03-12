package com.masin.pangea.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.masin.pangea.data.config.WebViewConfig
import com.masin.pangea.presentation.ui.screens.HomeScreen
import com.masin.pangea.presentation.ui.screens.LiaVoiceCallScreen
import com.masin.pangea.presentation.ui.screens.WebViewScreen

/**
 * Configuración de navegación de la aplicación
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.HOME,
        modifier = Modifier.padding(paddingValues)
    ) {
        // Pantalla de inicio
        composable(route = NavRoutes.HOME) {
            HomeScreen(
                onNavigateToConoce = {
                    navController.navigate(BottomNavItem.Conoce.route)
                },
                onNavigateToGestiona = {
                    navController.navigate(BottomNavItem.Gestiona.route)
                },
                onNavigateToSoluciona = {
                    navController.navigate(BottomNavItem.Soluciona.route)
                },
                onNavigateToPaga = {
                    navController.navigate(BottomNavItem.Paga.route)
                }
            )
        }
        
        composable(route = BottomNavItem.Conoce.route) {
            WebViewScreen(url = WebViewConfig.URL_CONOCE)
        }
        
        composable(route = BottomNavItem.Gestiona.route) {
            WebViewScreen(url = WebViewConfig.URL_GESTIONA)
        }
        
        composable(route = BottomNavItem.Soluciona.route) {
            WebViewScreen(url = WebViewConfig.URL_SOLUCIONA)
        }
        
        composable(route = BottomNavItem.Paga.route) {
            WebViewScreen(url = WebViewConfig.URL_PAGA)
        }

        // Pantalla de LIA (chat de voz)
        composable(route = NavRoutes.LIA) {
            LiaVoiceCallScreen(onBackPressed = { navController.popBackStack() })
        }
    }
}
