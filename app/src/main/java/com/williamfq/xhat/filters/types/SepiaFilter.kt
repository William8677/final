// com/williamfq/xhat/filters/types/SepiaFilter.kt
package com.williamfq.xhat.filters.types

import android.graphics.Bitmap
import com.williamfq.xhat.filters.base.Filter

class SepiaFilter : Filter() {
    override fun applyFilter(input: Any): Any {
        if (input is Bitmap) {
            return applySepiaEffect(input)
        }
        throw IllegalArgumentException("Input must be a Bitmap")
    }

    private fun applySepiaEffect(bitmap: Bitmap): Bitmap {
        // Lógica para aplicar el efecto sepia sobre una imagen (Bitmap)
        // Esta es una implementación básica, puedes optimizarla con librerías especializadas
        val width = bitmap.width
        val height = bitmap.height
        val sepiaBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val red = (pixel shr 16) and 0xFF
                val green = (pixel shr 8) and 0xFF
                val blue = pixel and 0xFF

                // Aplicar el efecto Sepia
                val tr = (0.393 * red + 0.769 * green + 0.189 * blue).toInt()
                val tg = (0.349 * red + 0.686 * green + 0.168 * blue).toInt()
                val tb = (0.272 * red + 0.534 * green + 0.131 * blue).toInt()

                val r = if (tr > 255) 255 else tr
                val g = if (tg > 255) 255 else tg
                val b = if (tb > 255) 255 else tb

                sepiaBitmap.setPixel(x, y, (0xFF shl 24) or (r shl 16) or (g shl 8) or b)
            }
        }
        return sepiaBitmap
    }

    override fun getDescription(): String {
        return "Sepia Filter"
    }
}
