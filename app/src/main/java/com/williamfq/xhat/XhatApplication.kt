package com.williamfq.xhat

import android.app.Application
import android.os.StrictMode
import androidx.multidex.BuildConfig
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.Arrays

@HiltAndroidApp
class XhatApplication : Application() {

    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }

        super.onCreate()
        initializeTimber()
        initializeAdMob()
    }

    private fun initializeTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    // Formato personalizado para los logs: [Clase:Línea]
                    return String.format(
                        "[%s:%s]",
                        super.createStackElementTag(element),
                        element.lineNumber
                    )
                }

                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // Agregar timestamp y usuario a los logs
                    val timeStamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                        java.util.Locale.getDefault()).format(java.util.Date())
                    val enhancedMessage = "[$timeStamp][User: William8677] $message"
                    super.log(priority, tag, enhancedMessage, t)
                }
            })
            Timber.d("Timber inicializado en modo DEBUG")
        } else {
            // Árbol de producción que solo registra errores importantes
            Timber.plant(object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority >= android.util.Log.WARN) {
                        // Aquí podrías implementar el envío a un servicio de crash reporting
                        // como Firebase Crashlytics
                    }
                }
            })
        }
    }

    private fun initializeAdMob() {
        try {
            // Configuración de AdMob para testing
            if (BuildConfig.DEBUG) {
                val testDeviceIds = Arrays.asList("TEST-DEVICE-ID")
                val configuration = RequestConfiguration.Builder()
                    .setTestDeviceIds(testDeviceIds)
                    .build()
                MobileAds.setRequestConfiguration(configuration)
            }

            MobileAds.initialize(this) { initializationStatus ->
                Timber.d("AdMob inicializado: $initializationStatus")
                initializationStatus.adapterStatusMap.forEach { (adapter, status) ->
                    Timber.d("Adapter $adapter: ${status.description} " +
                            "(Latency: ${status.latency}ms)")
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error al inicializar AdMob")
        }
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build()
        )

        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .detectActivityLeaks()
                .detectLeakedRegistrationObjects()
                .penaltyLog()
                .build()
        )

        Timber.d("StrictMode habilitado para desarrollo")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Timber.w("Memoria baja detectada")
        // Implementar limpieza de recursos
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Timber.d("Trim memory llamado con nivel: $level")
        when (level) {
            TRIM_MEMORY_RUNNING_MODERATE -> Timber.d("Memoria moderada")
            TRIM_MEMORY_RUNNING_LOW -> Timber.w("Memoria baja")
            TRIM_MEMORY_RUNNING_CRITICAL -> Timber.e("Memoria crítica")
        }
    }

    companion object {
        const val TAG = "XhatApplication"
        private const val TEST_DEVICE_ID = "TEST-DEVICE-ID" // Reemplazar con tu ID de dispositivo
    }
}