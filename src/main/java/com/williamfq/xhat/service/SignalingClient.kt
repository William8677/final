package com.williamfq.xhat.service

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.webrtc.*
import org.json.JSONObject
import okhttp3.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

class SignalingClient(
    private val roomId: String,
    private val userId: String,
    private val signalingServerUrl: String,
    private val webRTCListener: WebRTCListener
) : CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    private var webSocket: WebSocket? = null
    private val client = OkHttpClient()

    // Estado de la conexión
    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    // Mapa de peers conectados
    private val connectedPeers = ConcurrentHashMap<String, Boolean>()

    sealed class ConnectionState {
        object Disconnected : ConnectionState()
        object Connecting : ConnectionState()
        object Connected : ConnectionState()
        data class Error(val message: String) : ConnectionState()
    }

    // Interfaz para comunicar eventos de WebRTC
    interface WebRTCListener {
        fun onOfferReceived(offer: SessionDescription, fromUserId: String)
        fun onAnswerReceived(answer: SessionDescription, fromUserId: String)
        fun onIceCandidateReceived(iceCandidate: IceCandidate, fromUserId: String)
        fun onPeerDisconnected(userId: String)
        fun onError(error: String)
    }

    init {
        connectToSignalingServer()
    }

    private fun connectToSignalingServer() {
        _connectionState.value = ConnectionState.Connecting

        val request = Request.Builder()
            .url("$signalingServerUrl/room/$roomId/user/$userId")
            .build()

        webSocket = client.newWebSocket(request, createWebSocketListener())
    }

    private fun createWebSocketListener() = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            launch(Dispatchers.Main) {
                _connectionState.value = ConnectionState.Connected
                sendJoinRoom()
            }
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                handleSignalingMessage(text)
            } catch (e: Exception) {
                Log.e(TAG, "Error handling message: ${e.message}")
                webRTCListener.onError("Error processing message: ${e.message}")
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            launch(Dispatchers.Main) {
                _connectionState.value = ConnectionState.Disconnected
                connectedPeers.keys.forEach { webRTCListener.onPeerDisconnected(it) }
                connectedPeers.clear()
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            launch(Dispatchers.Main) {
                _connectionState.value = ConnectionState.Error(t.message ?: "Unknown error")
                webRTCListener.onError("WebSocket error: ${t.message}")
            }
        }
    }

    private fun handleSignalingMessage(message: String) {
        val json = JSONObject(message)
        when (json.getString("type")) {
            "offer" -> {
                val fromUserId = json.getString("fromUserId")
                val sdp = json.getString("sdp")
                val offer = SessionDescription(
                    SessionDescription.Type.OFFER,
                    sdp
                )
                webRTCListener.onOfferReceived(offer, fromUserId)
            }
            "answer" -> {
                val fromUserId = json.getString("fromUserId")
                val sdp = json.getString("sdp")
                val answer = SessionDescription(
                    SessionDescription.Type.ANSWER,
                    sdp
                )
                webRTCListener.onAnswerReceived(answer, fromUserId)
            }
            "ice_candidate" -> {
                val fromUserId = json.getString("fromUserId")
                val candidateJson = json.getJSONObject("candidate")
                val iceCandidate = IceCandidate(
                    candidateJson.getString("sdpMid"),
                    candidateJson.getInt("sdpMLineIndex"),
                    candidateJson.getString("candidate")
                )
                webRTCListener.onIceCandidateReceived(iceCandidate, fromUserId)
            }
            "user_left" -> {
                val userId = json.getString("userId")
                connectedPeers.remove(userId)
                webRTCListener.onPeerDisconnected(userId)
            }
        }
    }

    fun sendOffer(offer: SessionDescription, toUserId: String) {
        val message = JSONObject().apply {
            put("type", "offer")
            put("fromUserId", userId)
            put("toUserId", toUserId)
            put("sdp", offer.description)
        }
        sendMessage(message)
    }

    fun sendAnswer(answer: SessionDescription, toUserId: String) {
        val message = JSONObject().apply {
            put("type", "answer")
            put("fromUserId", userId)
            put("toUserId", toUserId)
            put("sdp", answer.description)
        }
        sendMessage(message)
    }

    fun sendIceCandidate(iceCandidate: IceCandidate, toUserId: String) {
        val message = JSONObject().apply {
            put("type", "ice_candidate")
            put("fromUserId", userId)
            put("toUserId", toUserId)
            put("candidate", JSONObject().apply {
                put("sdpMid", iceCandidate.sdpMid)
                put("sdpMLineIndex", iceCandidate.sdpMLineIndex)
                put("candidate", iceCandidate.sdp)
            })
        }
        sendMessage(message)
    }

    private fun sendJoinRoom() {
        val message = JSONObject().apply {
            put("type", "join_room")
            put("roomId", roomId)
            put("userId", userId)
        }
        sendMessage(message)
    }

    private fun sendMessage(message: JSONObject) {
        launch(Dispatchers.IO) {
            try {
                webSocket?.send(message.toString())
            } catch (e: Exception) {
                Log.e(TAG, "Error sending message: ${e.message}")
                _connectionState.value = ConnectionState.Error("Failed to send message: ${e.message}")
            }
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        job.cancel()
        _connectionState.value = ConnectionState.Disconnected
    }

    companion object {
        private const val TAG = "SignalingClient"
    }

    // Configuración de WebRTC
    object WebRTCConfig {
        val ICE_SERVERS = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer(),
            PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer()
            // Agrega más servidores STUN/TURN según sea necesario
        )

        val PEER_CONNECTION_CONSTRAINTS = MediaConstraints().apply {
            optional.add(MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }
    }
}