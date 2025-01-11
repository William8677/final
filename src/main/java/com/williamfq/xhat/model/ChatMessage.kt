// ChatMessage.kt: Entidad de mensajes para almacenamiento en la base de datos local
package com.williamfq.xhat.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chatId: String,
    val senderId: String,
    val messageType: String,
    val content: String,
    val timestamp: Long
)
