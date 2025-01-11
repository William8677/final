package com.williamfq.xhat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.williamfq.domain.model.Community
import com.williamfq.xhat.adapters.CommunityAdapter
import com.williamfq.xhat.databinding.ActivityCommunitiesBinding

class CommunitiesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunitiesBinding
    private lateinit var adapter: CommunityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadCommunities()
    }

    private fun setupRecyclerView() {
        adapter = CommunityAdapter(
            onSubscribeClick = { community ->
                handleSubscription(community)
            },
            onSettingsClick = { community ->
                openCommunitySettings(community)
            }
        )
        binding.rvCommunities.layoutManager = LinearLayoutManager(this)
        binding.rvCommunities.adapter = adapter
    }

    private fun loadCommunities() {
        val sampleCommunities = listOf(
            Community(1, "Comunidad 1", "Descripción 1", "Admin1", System.currentTimeMillis(), false, true, 120, listOf("Tech", "Coding")),
            Community(2, "Comunidad 2", "Descripción 2", "Admin2", System.currentTimeMillis(), true, true, 200, listOf("Health", "Fitness")),
        )
        adapter.submitList(sampleCommunities)
    }

    private fun handleSubscription(community: Community) {
        val updatedList = adapter.currentList.map {
            if (it.id == community.id) it.copy(isSubscribed = !it.isSubscribed) else it
        }
        adapter.submitList(updatedList)
    }

    private fun openCommunitySettings(community: Community) {
        // Implementar lógica para abrir configuración de la comunidad
    }
}
