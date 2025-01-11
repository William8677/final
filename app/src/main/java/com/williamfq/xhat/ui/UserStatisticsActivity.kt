// UserStatisticsActivity.kt: Actividad para mostrar estadísticas de uso del usuario
package com.williamfq.xhat.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.williamfq.xhat.R
import com.williamfq.xhat.manager.UserStatisticsManager

class UserStatisticsActivity : AppCompatActivity() {
    private lateinit var tvStatistics: TextView
    private val statsManager = UserStatisticsManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_statistics)

        tvStatistics = findViewById(R.id.tvStatistics)

        statsManager.getStatistics { stats ->
            if (stats != null) {
                val messagesSent = stats["messagesSent"] ?: 0
                tvStatistics.text = "Mensajes Enviados: $messagesSent"
            } else {
                tvStatistics.text = "No se pudieron cargar las estadísticas."
            }
        }
    }
}
