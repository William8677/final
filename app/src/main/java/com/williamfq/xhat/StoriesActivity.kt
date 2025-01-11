package com.williamfq.xhat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.williamfq.data.entities.MediaType
import com.williamfq.data.entities.Story
import com.williamfq.xhat.adapters.StoryAdapter
import com.williamfq.xhat.databinding.ActivityStoriesBinding

class StoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoriesBinding
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val stories = listOf(
            // Asegúrate de proporcionar todos los parámetros necesarios para Story
            Story(
                userId = "user1",
                title = "Title 1",
                description = "Content 1",
                mediaUrl = null,
                mediaType = MediaType.TEXT, // Correcto acceso a MediaType
                timestamp = System.currentTimeMillis(),
                isActive = true,
                views = 0,
                duration = 24,
                tags = emptyList(),
                comments = emptyList(),
                reactions = emptyList(),
                poll = null
            ),
            Story(
                userId = "user2",
                title = "Title 2",
                description = "Content 2",
                mediaUrl = null,
                mediaType = MediaType.TEXT, // Correcto acceso a MediaType
                timestamp = System.currentTimeMillis(),
                isActive = true,
                views = 0,
                duration = 24,
                tags = emptyList(),
                comments = emptyList(),
                reactions = emptyList(),
                poll = null
            )
        )
        adapter = StoryAdapter(stories)
        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = adapter
    }
}
