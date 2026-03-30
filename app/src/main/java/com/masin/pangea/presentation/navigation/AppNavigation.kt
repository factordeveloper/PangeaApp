package com.masin.pangea.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.masin.pangea.data.config.WebViewConfig
import com.masin.pangea.presentation.ui.screens.DeskScreen
import com.masin.pangea.presentation.ui.screens.ELearningScreen
import com.masin.pangea.presentation.ui.screens.HomeScreen
import com.masin.pangea.presentation.ui.screens.LiaVoiceCallScreen
import com.masin.pangea.presentation.ui.screens.WebViewScreen
import com.masin.pangea.presentation.ui.screens.WelcomeScreen
import com.masin.pangea.presentation.ui.screens.PlanSelectionScreen

import com.masin.pangea.presentation.ui.screens.WalkthroughScreen

/**
 * Configuración de navegación de la aplicación
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    paddingValues: PaddingValues,
    startDestination: String = NavRoutes.WELCOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(paddingValues)
    ) {
        // Recorrido Virtual
        composable(route = NavRoutes.WALKTHROUGH) {
            WalkthroughScreen(
                onFinishWalkthrough = {
                    navController.navigate(NavRoutes.WELCOME) {
                        popUpTo(NavRoutes.WALKTHROUGH) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Bienvenida
        composable(route = NavRoutes.WELCOME) {
            WelcomeScreen(
                onNavigateToHome = {
                    navController.navigate(NavRoutes.PLAN_SELECTION) {
                        popUpTo(NavRoutes.WELCOME) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Selección de Plan
        composable(route = NavRoutes.PLAN_SELECTION) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val sharedPrefs = androidx.compose.runtime.remember { context.getSharedPreferences("pangea_prefs", android.content.Context.MODE_PRIVATE) }
            PlanSelectionScreen(
                onNavigateToHome = { selectedPlan ->
                    val planName = selectedPlan?.name ?: "BASIC"
                    sharedPrefs.edit().putString("selected_plan", planName).apply()
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.PLAN_SELECTION) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de inicio
        composable(route = NavRoutes.HOME) {
            HomeScreen(
                onNavigateToPangea = {
                    navController.navigate(BottomNavItem.Pangea.route) {
                        launchSingleTop = true
                    }
                },
                onNavigateToELearning = {
                    navController.navigate(BottomNavItem.ELearning.route)
                },
                onNavigateToDesk = {
                    navController.navigate(BottomNavItem.Desk.route)
                },
                onNavigateToDigiturno = {
                    navController.navigate(BottomNavItem.Digiturno.route)
                },
                onNavigateToRadar = {
                    navController.navigate(NavRoutes.RADAR_TERRITORIAL)
                }
            )
        }
        
        composable(route = BottomNavItem.ELearning.route) {
            ELearningScreen()
        }
        
        composable(route = BottomNavItem.Desk.route) {
            DeskScreen()
        }
        
        composable(route = BottomNavItem.Digiturno.route) {
            WebViewScreen(url = WebViewConfig.URL_DIGITURNO)
        }

        // Pantalla de LIA (chat de voz)
        composable(route = NavRoutes.LIA) {
            LiaVoiceCallScreen(onBackPressed = { navController.popBackStack() })
        }

        // mRadar Territorial (LIA Explorer)
        composable(route = NavRoutes.RADAR_TERRITORIAL) {
            WebViewScreen(url = "file:///android_asset/lia-explorer/index.html")
        }
    }
}
