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
import com.masin.pangea.presentation.navigation.NavRoutes
import com.masin.pangea.presentation.ui.components.BottomNavigationBar
import com.masin.pangea.presentation.ui.components.NotificationsOverlay
import com.masin.pangea.presentation.ui.components.TopHeaderBar
import com.masin.pangea.presentation.ui.screens.PermissionsHandler
import com.masin.pangea.ui.theme.PANGEAappTheme
import kotlinx.coroutines.delay
import android.widget.Toast
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.masin.pangea.ui.theme.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.res.painterResource

// Duración del splash screen en milisegundos
private const val SPLASH_DURATION_MS = 3000L

class MainActivity : ComponentActivity() {
    
    companion object {
        // Constante pre-calculada - Color blanco para status bar
        private const val STATUS_BAR_COLOR = 0xFFFFFFFF.toInt()
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
                PermissionsHandler {
                    AppContent()
                }
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
            .background(Color(0xFFAC1926)),
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
    val navController = rememberNavController()
    var showNotifications by remember { mutableStateOf(false) }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val isImeVisible = imeBottom > 0
    
    val isLiaScreen = currentRoute == NavRoutes.LIA
    val hasChatInput = isLiaScreen
    val shouldShowBottomBar = !isImeVisible || !hasChatInput
    val shouldShowTopBar = !isImeVisible || !hasChatInput

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    val items = listOf(
        DrawerMenuItem("Home", Icons.Default.Home),
        DrawerMenuItem("Settings", Icons.Default.Settings),
        DrawerMenuItem("Share", Icons.Default.Share),
        DrawerMenuItem("About Us", Icons.Default.Info)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                Spacer(modifier = Modifier.height(8.dp))
                items.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
                        selected = item == items[selectedItem],
                        onClick = {
                            selectedItem = index
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = PangeaCyan.copy(alpha = 0.2f),
                            selectedIconColor = PangeaBlue,
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
                paddingValues = innerPadding
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
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(176.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(PangeaBlue, PangeaTeal, PangeaCyan, PangeaGreen)
                )
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Image(
            painter = painterResource(id = R.drawable.lia_profile),
            contentDescription = "User Profile",
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Asistente MASINA",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = "masin.com",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
    }
}

data class DrawerMenuItem(
    val title: String,
    val icon: ImageVector
)

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PANGEAappTheme {
        MainScreen()
    }
}