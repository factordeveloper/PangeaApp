package com.masin.pangea

import android.app.Application
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache

/**
 * Application class optimizada para rendimiento.
 * 
 * - Implementa ImageLoaderFactory para crear un singleton de ImageLoader
 * - Usa lazy initialization para evitar trabajo en el cold start
 * - Configura caché de memoria y disco para mejor rendimiento
 */
class PANGEAapp : Application(), ImageLoaderFactory {
    
    /**
     * ImageLoader singleton optimizado.
     * Se crea lazy (solo cuando se necesita por primera vez).
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                // Usar ImageDecoder en API 28+ (más eficiente)
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            // Caché de memoria: 25% de la memoria disponible
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            // Caché de disco: 50MB
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50 MB
                    .build()
            }
            // Crossfade deshabilitado para carga más rápida
            .crossfade(false)
            // Respetar cache headers para mejor rendimiento de red
            .respectCacheHeaders(true)
            .build()
    }
}
