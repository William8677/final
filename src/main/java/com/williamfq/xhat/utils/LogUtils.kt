// utils/LogUtils.kt
package com.williamfq.xhat.utils

import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogUtils @Inject constructor() {
    fun logDebug(message: String) {
        Timber.d(message)
    }

    fun logInfo(message: String) {
        Timber.i(message)
    }

    fun logWarning(message: String) {
        Timber.w(message)
    }

    fun logError(message: String, error: Throwable? = null) {
        Timber.e(error, message)
    }

    fun logSuccess(message: String) {
        Timber.i("✓ $message")
    }
}