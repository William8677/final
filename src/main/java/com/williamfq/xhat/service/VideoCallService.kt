package com.williamfq.xhat.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.williamfq.xhat.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.webrtc.*
import java.util.concurrent.Executors

class VideoCallService : LifecycleService() {
    private val binder = LocalBinder()
    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var videoCapturer: VideoCapturer? = null
    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null
    private var remoteVideoTrack: VideoTrack? = null
    private lateinit var eglBaseContext: EglBase.Context
    private lateinit var signalingClient: SignalingClient
    private val executor = Executors.newSingleThreadExecutor()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _callState = MutableStateFlow<CallState>(CallState.Idle)
    val callState = _callState.asStateFlow()

    inner class LocalBinder : Binder() {
        fun getService(): VideoCallService = this@VideoCallService
    }

    sealed class CallState {
        object Idle : CallState()
        object Connecting : CallState()
        object Connected : CallState()
        data class Error(val message: String) : CallState()
        object Disconnected : CallState()
    }

    companion object {
        private const val TAG = "VideoCallService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "VideoCall"
    }

    override fun onCreate() {
        super.onCreate()
        initializeService()
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    private fun initializeService() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        initializePeerConnectionFactory()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Video Call Service",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Ongoing video call notification"
                setSound(null, null)
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Llamada de video activa")
            .setContentText("Toca para volver a la llamada")
            .setSmallIcon(R.drawable.ic_videocam)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()
    }

    private fun initializePeerConnectionFactory() {
        try {
            val options = PeerConnectionFactory.InitializationOptions.builder(this)
                .setEnableInternalTracer(true)
                .createInitializationOptions()

            PeerConnectionFactory.initialize(options)
            eglBaseContext = EglBase.create().eglBaseContext

            val encoderFactory = DefaultVideoEncoderFactory(eglBaseContext, true, true)
            val decoderFactory = DefaultVideoDecoderFactory(eglBaseContext)

            peerConnectionFactory = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .setOptions(PeerConnectionFactory.Options().apply {
                    disableEncryption = false
                    disableNetworkMonitor = false
                })
                .createPeerConnectionFactory()

            _callState.value = CallState.Idle
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing PeerConnectionFactory: ${e.message}")
            _callState.value = CallState.Error("Error initializing video call service")
        }
    }
}
