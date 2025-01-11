
package com.williamfq.xhat.chatrooms

import com.google.firebase.firestore.FirebaseFirestore

class ChatRoomManager {

    private val db = FirebaseFirestore.getInstance()

    fun createRoom(name: String, description: String, category: String, createdBy: String, avatarUrl: String) {
        val roomData = mapOf(
            "name" to name,
            "description" to description,
            "category" to category,
            "createdBy" to createdBy,
            "avatarUrl" to avatarUrl,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("chatRooms").add(roomData)
    }

    fun sendMessage(roomId: String, nickname: String, content: String, type: String = "public", replyTo: String? = null) {
        val messageData = mapOf(
            "content" to content,
            "sender" to nickname,
            "type" to type,
            "replyTo" to replyTo,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("messages").document(roomId).collection("messages").add(messageData)
    }
}
