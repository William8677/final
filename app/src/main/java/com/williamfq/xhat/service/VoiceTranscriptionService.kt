// VoiceTranscriptionService.kt: Servicio para transcribir mensajes de voz automáticamente
package com.williamfq.xhat.service

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class VoiceTranscriptionService : Service() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var db: FirebaseFirestore

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        db = FirebaseFirestore.getInstance()
        initializeSpeechRecognizer()
        return START_STICKY
    }

    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("VoiceTranscriptionService", "Listo para escuchar")
            }

            override fun onBeginningOfSpeech() {
                Log.d("VoiceTranscriptionService", "Comenzó la detección de voz")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // No es necesario hacer nada aquí
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                // No es necesario hacer nada aquí
            }

            override fun onEndOfSpeech() {
                Log.d("VoiceTranscriptionService", "Fin de la detección de voz")
            }

            override fun onError(error: Int) {
                Log.e("VoiceTranscriptionService", "Error de reconocimiento: $error")
            }

            override fun onResults(results: Bundle?) {
                results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                    for (result in it) {
                        Log.d("VoiceTranscriptionService", "Texto detectado: $result")
                        saveTranscriptionToDatabase(result)
                    }
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                // No es necesario hacer nada aquí
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                // No es necesario hacer nada aquí
            }
        })

        speechRecognizer.startListening(recognizerIntent)
    }

    private fun saveTranscriptionToDatabase(transcription: String) {
        val transcriptionData = hashMapOf(
            "transcription" to transcription,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("voice_transcriptions").add(transcriptionData)
            .addOnSuccessListener {
                Log.d("VoiceTranscriptionService", "Transcripción guardada exitosamente: ${it.id}")
            }
            .addOnFailureListener {
                Log.e("VoiceTranscriptionService", "Error al guardar la transcripción: ${it.message}")
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
        Log.d("VoiceTranscriptionService", "Servicio destruido")
    }
}
