// File: app/src/main/java/com/williamfq/xhat/ui/OfflineModeActivity.kt
package com.williamfq.xhat.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.williamfq.xhat.R
import com.williamfq.xhat.adapters.ChatAdapter
import com.williamfq.data.local.LocalDatabase
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

/**
 * Activity para el modo sin conexión
 * @author William8677
 * @created 2025-01-04 18:02:44
 */
class OfflineModeActivity : AppCompatActivity() {
    private lateinit var rvMessages: RecyclerView
    private lateinit var chatAdapter: ChatAdapter
    private val localDatabase = LocalDatabase.getInstance(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_mode)

        setupRecyclerView()
        loadMessages()
    }

    private fun setupRecyclerView() {
        rvMessages = findViewById(R.id.rvMessages)
        chatAdapter = ChatAdapter()
        rvMessages.apply {
            layoutManager = LinearLayoutManager(this@OfflineModeActivity)
            adapter = chatAdapter
        }
    }

    private fun loadMessages() {
        lifecycleScope.launch {
            localDatabase.messageDao().getAllMessages().collectLatest { messages ->
                chatAdapter.submitList(messages)
            }
        }
    }
}