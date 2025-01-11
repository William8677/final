// com/williamfq/xhat/filters/processor/FilterUtils.kt
package com.williamfq.xhat.filters.processor

import android.graphics.Bitmap
import com.williamfq.xhat.filters.base.Filter

object FilterUtils {
    fun applyMultipleFilters(filters: List<Filter>, image: Bitmap): Bitmap {
        var filteredImage = image
        for (filter in filters) {
            filteredImage = FilterProcessor.applyFilterToImage(filter, filteredImage)
        }
        return filteredImage
    }
}
