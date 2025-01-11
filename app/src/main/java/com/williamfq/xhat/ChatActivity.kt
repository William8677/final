package com.williamfq.xhat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.williamfq.xhat.databinding.ActivityChatBinding
import com.williamfq.xhat.presentation.viewmodel.ChatViewModel

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var viewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        // Configura el adaptador, RecyclerView y otros elementos de UI aquí
    }

    private fun setupObservers() {
        viewModel.chatMessages.observe(this) { messages ->
            // Actualiza la UI con los mensajes observados
        }
    }
}
