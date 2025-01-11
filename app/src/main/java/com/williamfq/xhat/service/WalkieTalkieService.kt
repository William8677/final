package com.williamfq.xhat.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class WalkieTalkieService : Service() {

    private val binder = WalkieTalkieBinder()

    inner class WalkieTalkieBinder : Binder() {
        fun getService(): WalkieTalkieService = this@WalkieTalkieService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    fun startWalkieTalkie() {
        // Lógica para iniciar la función de walkie-talkie
    }

    fun stopWalkieTalkie() {
        // Lógica para detener la función de walkie-talkie
    }
}
