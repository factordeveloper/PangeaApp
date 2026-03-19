package com.masin.pangea.presentation.ui.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.masin.pangea.R

/**
 * Pantalla optimizada que muestra un WebView con la URL especificada.
 *
 * Optimizaciones:
 * - WebView se crea una sola vez con remember
 * - Settings optimizados para rendimiento
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    
    // Estado para manejar la selección de archivos (nombre distinto al parámetro de onShowFileChooser)
    var pendingFileCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }
    
    // Launcher para seleccionar archivos (debe ir antes de permissionLauncher)
    val fileChooserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val uris = if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.let { intent ->
                val clipData = intent.clipData
                if (clipData != null) {
                    Array(clipData.itemCount) { i -> clipData.getItemAt(i).uri }
                } else {
                    intent.data?.let { arrayOf(it) }
                }
            }
        } else null
        
        pendingFileCallback?.onReceiveValue(uris)
        pendingFileCallback = null
    }
    
    // Launcher para solicitar permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }
            try {
                fileChooserLauncher.launch(intent)
            } catch (e: Exception) {
                pendingFileCallback?.onReceiveValue(null)
                pendingFileCallback = null
            }
        } else {
            pendingFileCallback?.onReceiveValue(null)
            pendingFileCallback = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    val webView = this
                    CookieManager.getInstance().apply {
                        setAcceptCookie(true)
                        setAcceptThirdPartyCookies(webView, true)
                    }

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        builtInZoomControls = true
                        displayZoomControls = false
                        setSupportZoom(true)
                        allowFileAccess = true
                        allowContentAccess = true
                        cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                        setGeolocationEnabled(false)
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            isLoading = true
                            hasError = false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false

                            val cookieScript = """
                                (function() {
                                    function hideCookieBanners() {
                                        var allButtons = document.querySelectorAll('button, a, div[role="button"], span[role="button"]');
                                        for (var i = 0; i < allButtons.length; i++) {
                                            var btn = allButtons[i];
                                            var text = (btn.innerText || btn.textContent || '').toLowerCase().trim();
                                            if (text === 'aceptar todo' || text === 'accept all' || 
                                                text === 'aceptar' || text === 'accept' ||
                                                text === 'acepto' || text === 'i agree' ||
                                                text === 'agree' || text === 'ok' ||
                                                text === 'got it' || text === 'entendido') {
                                                btn.click();
                                                return true;
                                            }
                                        }
                                        var selectors = [
                                            '[class*="cookie"]', '[id*="cookie"]',
                                            '[class*="consent"]', '[id*="consent"]',
                                            '[class*="gdpr"]', '[id*="gdpr"]',
                                            '[class*="privacy-banner"]', '[id*="privacy-banner"]',
                                            '.cc-window', '#cc-window',
                                            '[class*="notice-banner"]', '[id*="notice-banner"]'
                                        ];
                                        selectors.forEach(function(selector) {
                                            try {
                                                var elements = document.querySelectorAll(selector);
                                                elements.forEach(function(el) {
                                                    var text = (el.innerText || '').toLowerCase();
                                                    if (text.includes('cookie') || text.includes('privacidad') || 
                                                        text.includes('privacy') || text.includes('consent')) {
                                                        el.style.display = 'none';
                                                        el.style.visibility = 'hidden';
                                                    }
                                                });
                                            } catch(e) {}
                                        });
                                        return false;
                                    }
                                    hideCookieBanners();
                                    setTimeout(hideCookieBanners, 500);
                                    setTimeout(hideCookieBanners, 1000);
                                    setTimeout(hideCookieBanners, 2000);
                                })();
                            """.trimIndent()
                            view?.evaluateJavascript(cookieScript, null)
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            if (request?.isForMainFrame == true) {
                                isLoading = false
                                hasError = true
                            }
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onShowFileChooser(
                            webView: WebView?,
                            filePathCallback: ValueCallback<Array<Uri>>?,
                            fileChooserParams: FileChooserParams?
                        ): Boolean {
                            // Cancelar cualquier callback previo
                            pendingFileCallback?.onReceiveValue(null)
                            pendingFileCallback = filePathCallback
                            
                            // Verificar permisos necesarios según la versión de Android
                            val permissionsToRequest = mutableListOf<String>()
                            
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                // Android 13+ requiere permisos específicos por tipo de medio
                                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                                    permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_IMAGES)
                                }
                                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                                    permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_VIDEO)
                                }
                                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                    permissionsToRequest.add(android.Manifest.permission.READ_MEDIA_AUDIO)
                                }
                            } else {
                                // Android 12 y anteriores
                                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                            }
                            
                            // Si necesitamos solicitar permisos
                            if (permissionsToRequest.isNotEmpty()) {
                                permissionLauncher.launch(permissionsToRequest.toTypedArray())
                                return true
                            }
                            
                            // Crear intent para seleccionar archivos
                            val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                                type = "*/*"
                                addCategory(Intent.CATEGORY_OPENABLE)
                                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                            }
                            
                            try {
                                fileChooserLauncher.launch(intent)
                            } catch (e: Exception) {
                                pendingFileCallback?.onReceiveValue(null)
                                pendingFileCallback = null
                                return false
                            }
                            
                            return true
                        }
                    }
                    loadUrl(url)
                }
            },
            update = { webView ->
                if (webView.url != url) {
                    webView.loadUrl(url)
                }
            }
        )

        if (isLoading && !hasError) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0D5C5C)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(if (isTablet) R.drawable.loading_tablet else R.drawable.loading)
                        .crossfade(false)
                        .build(),
                    contentDescription = "Cargando...",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        if (hasError) {
            Image(
                painter = painterResource(id = R.drawable.no_internet),
                contentDescription = "Sin conexión a internet",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}
