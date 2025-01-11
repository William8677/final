package com.williamfq.xhat

import android.content.Context
import android.media.MediaPlayer

class VoiceNotePlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playVoiceNote(filePath: String) {
        stopPlayback()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(filePath)
            prepare()
            start()
        }
    }

    fun stopPlayback() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
