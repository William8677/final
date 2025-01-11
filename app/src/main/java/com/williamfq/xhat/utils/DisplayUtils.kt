package com.williamfq.xhat.utils

import android.widget.TextView
import android.util.Log

object DisplayUtils {

    // Método para mostrar subtítulos en un TextView
    fun displaySubtitle(subtitle: String, textView: TextView) {
        try {
            // Actualiza el texto en el TextView
            textView.text = subtitle
            Log.d("DisplayUtils", "Subtítulo mostrado: $subtitle")
        } catch (e: Exception) {
            Log.e("DisplayUtils", "Error al mostrar el subtítulo: ${e.message}")
        }
    }
}
