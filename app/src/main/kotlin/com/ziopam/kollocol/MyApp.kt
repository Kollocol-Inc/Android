package com.ziopam.kollocol

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application() {

    @Inject
    lateinit var okHttpClient: OkHttpClient

    override fun onCreate() {
        super.onCreate()
        Coil.setImageLoader {
            ImageLoader.Builder(this)
                .okHttpClient(okHttpClient)
                .diskCache {
                    DiskCache.Builder()
                        .directory(filesDir.resolve("image_cache"))
                        .maxSizeBytes(50L * 1024 * 1024)
                        .build()
                }
                .build()
        }
    }
}
