package com.williamfq.xhat

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.williamfq.xhat.ui.theme.XhatTheme
import com.williamfq.xhat.utils.ErrorHandler
import com.williamfq.xhat.utils.LogUtils
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: PermissionsViewModel by viewModels()

    @Inject
    lateinit var errorHandler: ErrorHandler

    @Inject
    lateinit var logUtils: LogUtils

    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    companion object {
        private const val TAG = "MainActivity"
        private const val CURRENT_USER = "William8677"
        private const val TIMESTAMP_UTC = "2025-01-11 01:30:18"
        private const val TASK_ID = "784"  // Agregado basado en el log
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            installSplashScreen()
            super.onCreate(savedInstanceState)

            // Log seguro después de super.onCreate()
            logActivityEvent("onCreate", "iniciando [TaskId: $TASK_ID]")

            setupPermissionLauncher()
            initializeNotifications()
            initializePermissionsHandling()
            setupUserInterface()

        } catch (e: Exception) {
            // Manejo seguro de errores iniciales
            Toast.makeText(this, getString(R.string.error_generic), Toast.LENGTH_LONG).show()
            if (::logUtils.isInitialized) {
                handleError(e, "onCreate")
            }
        }
    }

    private fun setupPermissionLauncher() {
        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            handleNotificationPermissionResult(isGranted)
        }
    }

    private fun initializeNotifications() {
        try {
            createNotificationChannel()
            checkNotificationPermission()
            logUtils.logSuccess("Inicialización de notificaciones completada [Usuario: $CURRENT_USER, TaskId: $TASK_ID]")
        } catch (e: Exception) {
            handleError(e, "initializeNotifications")
            showUserMessage(getString(R.string.error_notifications_initialization))
        }
    }

    private fun initializePermissionsHandling() {
        val permissionsLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            logUtils.logDebug("Resultado de permisos recibido: $permissions [TaskId: $TASK_ID]")
            viewModel.updatePermissionsState(permissions)
        }

        viewModel.initializePermissionsLauncher { permissions ->
            logUtils.logDebug("Lanzando solicitud de permisos: ${permissions.joinToString()} [TaskId: $TASK_ID]")
            permissionsLauncher.launch(permissions.toList().toTypedArray())
        }
    }

    private fun setupUserInterface() {
        setContent {
            logUtils.logDebug("Configurando UI principal [Usuario: $CURRENT_USER, TaskId: $TASK_ID]")
            XhatTheme {
                val permissionsGranted by viewModel.permissionsGranted.collectAsState()

                MainScreen(
                    permissionsGranted = permissionsGranted,
                    onRequestPermissions = {
                        logUtils.logDebug("Solicitud de permisos iniciada desde UI [TaskId: $TASK_ID]")
                        viewModel.requestPermissionsIfNeeded()
                    }
                )
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            logUtils.logDebug("${getString(R.string.log_permissions_requested)} [TaskId: $TASK_ID]")
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                setupNotifications()
            }
        } else {
            setupNotifications()
        }
    }

    private fun handleNotificationPermissionResult(isGranted: Boolean) {
        if (isGranted) {
            logUtils.logInfo("${getString(R.string.log_permissions_granted)} [TaskId: $TASK_ID]")
            setupNotifications()
            showUserMessage(getString(R.string.success_notifications_enabled))
        } else {
            logUtils.logWarning("${getString(R.string.log_permissions_denied)} [TaskId: $TASK_ID]")
            showUserMessage(getString(R.string.error_notification_permission_denied))
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val channel = NotificationChannel(
                    "XhatChannel",
                    getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = getString(R.string.notification_channel_description)
                }

                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
                logUtils.logSuccess("Canal de notificaciones creado exitosamente [TaskId: $TASK_ID]")
            } catch (e: Exception) {
                handleError(e, "createNotificationChannel")
            }
        }
    }

    private fun setupNotifications() {
        try {
            logUtils.logSuccess("Sistema de notificaciones configurado correctamente [TaskId: $TASK_ID]")
        } catch (e: Exception) {
            handleError(e, "setupNotifications")
        }
    }

    private fun handleError(error: Exception, source: String) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        logUtils.logError("Error en $source - $timestamp [Usuario: $CURRENT_USER, TaskId: $TASK_ID]", error)
        errorHandler.handleException(error)
        showUserMessage(getString(R.string.error_generic))
    }

    private fun showUserMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        if (::logUtils.isInitialized) {
            logUtils.logDebug("Mensaje mostrado al usuario: $message [TaskId: $TASK_ID]")
        }
    }

    private fun logActivityEvent(event: String, details: String) {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
        logUtils.logInfo("$event - $details [Usuario: $CURRENT_USER, UTC: $TIMESTAMP_UTC, TaskId: $TASK_ID]")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::logUtils.isInitialized) {
            logActivityEvent("onDestroy", "finalizando")
        }
    }
}