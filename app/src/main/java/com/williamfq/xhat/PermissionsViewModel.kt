package com.williamfq.xhat

import android.Manifest
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PermissionsViewModel @Inject constructor() : ViewModel() {

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted

    private lateinit var requestPermissionsLauncher: (Array<String>) -> Unit

    val requiredPermissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    init {
        Timber.d("PermissionsViewModel inicializado")
    }

    fun initializePermissionsLauncher(launcher: (Array<String>) -> Unit) {
        requestPermissionsLauncher = launcher
        Timber.d("Launcher de permisos inicializado")
    }

    fun updatePermissionsState(permissions: Map<String, Boolean>) {
        val allGranted = permissions.values.all { it }
        _permissionsGranted.value = allGranted
        Timber.d("Estado de permisos actualizado: todos concedidos = $allGranted")

        // Log detallado de permisos
        permissions.forEach { (permission, isGranted) ->
            Timber.v("Permiso $permission: ${if (isGranted) "concedido" else "denegado"}")
        }
    }

    fun requestPermissionsIfNeeded() {
        if (!_permissionsGranted.value) {
            if (::requestPermissionsLauncher.isInitialized) {
                Timber.i("Solicitando permisos...")
                requestPermissionsLauncher.invoke(requiredPermissions)
            } else {
                Timber.e("Error: El launcher de permisos no está inicializado")
            }
        } else {
            Timber.d("Los permisos ya están concedidos")
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("PermissionsViewModel destruido")
    }
}