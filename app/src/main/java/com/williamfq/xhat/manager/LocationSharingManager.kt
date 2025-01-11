// LocationSharingManager.kt: Clase para compartir ubicación en tiempo real
package com.williamfq.xhat.manager

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.content.Context

class LocationSharingManager(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun shareLocation(chatId: String) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val locationData = hashMapOf(
                    "latitude" to location.latitude,
                    "longitude" to location.longitude,
                    "timestamp" to System.currentTimeMillis()
                )
                // Lógica para compartir la ubicación en el chat especificado
                println("Ubicación compartida: Latitud ${location.latitude}, Longitud ${location.longitude}")
            }
        }.addOnFailureListener {
            println("Error al obtener la ubicación: ${it.message}")
        }
    }
}