// com/williamfq/xhat/filters/types/GrayscaleFilter.kt
package com.williamfq.xhat.filters.types

import android.graphics.Bitmap
import com.williamfq.xhat.filters.base.Filter

class GrayscaleFilter : Filter() {
    override fun applyFilter(input: Any): Any {
        if (input is Bitmap) {
            return applyGrayscaleEffect(input)
        }
        throw IllegalArgumentException("Input must be a Bitmap")
    }

    private fun applyGrayscaleEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val red = (pixel shr 16) and 0xFF
                val green = (pixel shr 8) and 0xFF
                val blue = pixel and 0xFF

                // Calcular el valor en escala de grises
                val gray = (red + green + blue) / 3
                grayscaleBitmap.setPixel(x, y, (0xFF shl 24) or (gray shl 16) or (gray shl 8) or gray)
            }
        }
        return grayscaleBitmap
    }

    override fun getDescription(): String {
        return "Grayscale Filter"
    }
}
