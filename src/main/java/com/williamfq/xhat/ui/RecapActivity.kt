package com.xhat.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.williamfq.domain.model.UserActivityData
import com.williamfq.xhat.R

class RecapActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvMessagesText: TextView
    private lateinit var tvMessagesVoice: TextView
    private lateinit var tvStoriesCount: TextView
    // ... más TextViews según necesites

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recap)

        // 1. Referencias a las vistas
        tvTitle = findViewById(R.id.tvTitle)
        tvMessagesText = findViewById(R.id.tvMessagesText)
        tvMessagesVoice = findViewById(R.id.tvMessagesVoice)
        tvStoriesCount = findViewById(R.id.tvStoriesCount)
        // ...

        // 2. Recibir datos (puedes pasarlos por intent o desde un backend)
        //    Aquí lo simularemos con datos ficticios:
        val simulatedData = UserActivityData(
            messagesText = 120,
            messagesVoice = 10,
            storiesCount = 5,
            storyReactions = 40,
            callTimeVoice = 180,
            callTimeVideo = 60,
            topContactName = "Juan Pérez",
            topContactMessageCount = 50
        )

        showRecap(simulatedData, period = "weekly")
    }

    private fun showRecap(data: UserActivityData, period: String) {
        // 3. Define el título según el período
        tvTitle.text = when (period) {
            "weekly" -> "Esta fue tu semana en Xhat..."
            "monthly" -> "Este fue tu mes en Xhat..."
            "yearly"  -> "Este fue tu año en Xhat..."
            else      -> "Tu resumen de actividad en Xhat..."
        }

        // 4. Muestra los datos
        tvMessagesText.text = "Mensajes de texto enviados: ${data.messagesText}"
        tvMessagesVoice.text = "Mensajes de voz enviados: ${data.messagesVoice}"
        tvStoriesCount.text = "Historias subidas: ${data.storiesCount} (¡${data.storyReactions} reacciones!)"

        // Continúa mostrando las demás estadísticas
        // ...
    }
}
