// File: app/src/main/java/com/williamfq/xhat/adapters/ChatAdapter.kt
package com.williamfq.xhat.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.williamfq.data.entities.MessageEntity
import com.williamfq.xhat.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adaptador para mostrar mensajes de chat en el modo sin conexión
 * @author William8677
 * @created 2025-01-04 18:02:44
 */
class ChatAdapter : ListAdapter<MessageEntity, ChatAdapter.MessageViewHolder>(MessageDiffCallback()) {

    private var currentUserId: String = ""

    fun setCurrentUserId(userId: String) {
        currentUserId = userId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layout = when (viewType) {
            VIEW_TYPE_SENT -> R.layout.item_message_sent
            else -> R.layout.item_message_received
        }
        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderId == currentUserId) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)

        fun bind(message: MessageEntity) {
            tvMessage.text = if (message.isDeleted) {
                itemView.context.getString(R.string.deleted_message)
            } else {
                message.message
            }

            tvTime.text = formatTimestamp(message.timestamp)

            if (message.senderId == currentUserId) {
                tvStatus.visibility = View.VISIBLE
                tvStatus.text = when {
                    message.isDeleted -> "🗑️"
                    !message.isSent -> "🕒"
                    message.isRead -> "✓✓"
                    else -> "✓"
                }
            } else {
                tvStatus.visibility = View.GONE
            }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
    }

    private class MessageDiffCallback : DiffUtil.ItemCallback<MessageEntity>() {
        override fun areItemsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }
}