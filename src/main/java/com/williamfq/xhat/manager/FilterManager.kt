// com/williamfq/xhat/manager/FilterManager.kt
package com.williamfq.xhat.manager

import android.content.Context
import android.graphics.Bitmap
import com.williamfq.xhat.filters.base.Filter
import com.williamfq.xhat.filters.processor.FilterProcessor
import com.williamfq.xhat.filters.processor.FilterUtils

class FilterManager(private val context: Context) {
    fun applyFilter(filter: Filter, image: Bitmap): Bitmap {
        return FilterProcessor.applyFilterToImage(filter, image)
    }

    fun applyMultipleFilters(filters: List<Filter>, image: Bitmap): Bitmap {
        return FilterUtils.applyMultipleFilters(filters, image)
    }
}
