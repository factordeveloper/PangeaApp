package com.masin.pangea

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.masin.pangea.presentation.navigation.AppNavigation
import com.masin.pangea.presentation.navigation.BottomNavItem
import com.masin.pangea.presentation.navigation.NavRoutes
import com.masin.pangea.presentation.ui.components.BottomNavigationBar
import com.masin.pangea.presentation.ui.components.NotificationsOverlay
import com.masin.pangea.presentation.ui.components.TopHeaderBar
import com.masin.pangea.ui.theme.PANGEAappTheme
import kotlinx.coroutines.delay
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.masin.pangea.ui.theme.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.res.painterResource

// Duración del splash screen en milisegundos
private const val SPLASH_DURATION_MS = 3000L

class MainActivity : ComponentActivity() {
    
    companion object {
        // Constante pre-calculada - Color blanco para status bar
        private const val STATUS_BAR_COLOR = 0xFF0D5C5C.toInt()
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Instalar splash screen ANTES de super.onCreate()
        // Esto usa la API nativa que es mucho más rápida
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Configurar edge-to-edge con status bar blanca e iconos oscuros
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(STATUS_BAR_COLOR, STATUS_BAR_COLOR)
        )

        setContent {
            PANGEAappTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    var showSplash by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        delay(SPLASH_DURATION_MS)
        showSplash = false
    }
    
    if (showSplash) {
        SplashScreen()
    } else {
        MainScreen()
    }
}

@Composable
fun SplashScreen() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val splashDrawable = if (isLandscape) R.drawable.splashscreentablet else R.drawable.splashscreen
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D5C5C)), 
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(splashDrawable)
                .crossfade(false)
                .build(),
            contentDescription = "Splash Screen",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("pangea_prefs", android.content.Context.MODE_PRIVATE) }
    val isFirstRun = remember { sharedPrefs.getBoolean("is_first_run", true) }
    val startDestination = if (isFirstRun) NavRoutes.WALKTHROUGH else NavRoutes.WELCOME

    val navController = rememberNavController()
    var showNotifications by remember { mutableStateOf(false) }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val isImeVisible = imeBottom > 0
    
    val isLiaScreen = currentRoute == NavRoutes.LIA
    val isWelcomeScreen = currentRoute == NavRoutes.WELCOME
    val isWalkthroughScreen = currentRoute == NavRoutes.WALKTHROUGH
    val isPlanSelectionScreen = currentRoute == NavRoutes.PLAN_SELECTION
    val hasChatInput = isLiaScreen
    val shouldShowBottomBar = (!isImeVisible || !hasChatInput) && !isWelcomeScreen && !isPlanSelectionScreen && !isWalkthroughScreen
    val shouldShowTopBar = (!isImeVisible || !hasChatInput) && !isWelcomeScreen && !isPlanSelectionScreen && !isWalkthroughScreen

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader(
                    onCloseClick = { scope.launch { drawerState.close() } }
                )
                Spacer(modifier = Modifier.height(8.dp))
                BottomNavItem.items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = item.iconResId),
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp),
                                tint = if (isSelected) item.selectedColor else Color.Gray
                            )
                        },
                        label = { Text(item.title) },
                        selected = isSelected,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(NavRoutes.HOME) {
                                        saveState = true
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = PangeaCyan.copy(alpha = 0.2f),
                            selectedIconColor = item.selectedColor,
                            selectedTextColor = PangeaBlue,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.DarkGray
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        Toast.makeText(context, "Logout!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.DarkGray
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = if (hasChatInput && isImeVisible) {
                WindowInsets.ime
            } else {
                WindowInsets(0.dp)
            },
            topBar = {
                if (shouldShowTopBar) {
                    TopHeaderBar(
                        navController = navController,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onNotificationsClick = { showNotifications = !showNotifications }
                    )
                }
            },
            bottomBar = {
                if (shouldShowBottomBar) {
                    BottomNavigationBar(navController = navController)
                }
            }
        ) { innerPadding ->
            AppNavigation(
                navController = navController,
                paddingValues = innerPadding,
                startDestination = startDestination
            )
        }

        // Overlay de notificaciones: encima de todo (incluyendo header)
        if (showNotifications) {
            NotificationsOverlay(onDismiss = { showNotifications = false })
        }
    }
    }
}

@Composable
fun DrawerHeader(onCloseClick: () -> Unit) {
    val dimens = com.masin.pangea.presentation.ui.utils.rememberAppDimens()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimens.avatarSize * 2.8f)
            .background(
                Brush.linearGradient(
                    0f to PangeaBlue,
                    0.32f to PangeaTeal,
                    0.58f to Color(0xFF003D5C),
                    1f to Color(0xFF001A26)
                )
            )
            .padding(horizontal = dimens.spacingSmall, vertical = dimens.spacingSmall)
    ) {
        IconButton(
            onClick = onCloseClick,
            modifier = Modifier.padding(0.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Cerrar menú",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = dimens.spacingSmall, end = dimens.spacingSmall)
            ) {
                Text(
                    text = "Asistente LIA",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = dimens.fontBody
                )
                Text(
                    text = "Pangea App",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = dimens.fontCaption
                )
            }
            Image(
                painter = painterResource(id = R.drawable.lia_profile),
                contentDescription = "Libélula LIA",
                modifier = Modifier.size(dimens.avatarSize * 1.3f),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PANGEAappTheme {
        MainScreen()
    }
}