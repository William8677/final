// com/williamfq/xhat/filters/processor/FilterProcessor.kt
package com.williamfq.xhat.filters.processor

import android.graphics.Bitmap
import com.williamfq.xhat.filters.base.Filter

object FilterProcessor {
    fun applyFilterToImage(filter: Filter, image: Bitmap): Bitmap {
        return filter.applyFilter(image) as Bitmap
    }
}
