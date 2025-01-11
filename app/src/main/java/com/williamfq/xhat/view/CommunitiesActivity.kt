package com.williamfq.xhat.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.williamfq.domain.model.Community
import com.williamfq.domain.model.CommunityResult
import com.williamfq.domain.model.Visibility
import com.williamfq.xhat.R
import com.williamfq.xhat.adapters.CommunityAdapter
import com.williamfq.xhat.databinding.ActivityCommunitiesBinding
import com.williamfq.xhat.databinding.DialogCreateCommunityBinding
import com.williamfq.xhat.viewmodel.CommunitiesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant

@AndroidEntryPoint
class CommunitiesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCommunitiesBinding
    private val viewModel: CommunitiesViewModel by viewModels()
    private lateinit var adapter: CommunityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommunitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupUI() {
        binding.apply {
            btnCreateCommunity.setOnClickListener {
                showCreateCommunityDialog()
            }

            topBar.setOnClickListener {
                rvCommunities.smoothScrollToPosition(0)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = CommunityAdapter(
            onSubscribeClick = { community ->
                viewModel.subscribeToCommunity(community.id.toString())
            },
            onSettingsClick = { community ->
                if (community.hasAdminPermissions("William8677")) {
                    showCommunitySettings(community)
                } else {
                    Toast.makeText(
                        this,
                        "No tienes permisos para administrar esta comunidad",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        binding.rvCommunities.apply {
            layoutManager = LinearLayoutManager(this@CommunitiesActivity)
            adapter = this@CommunitiesActivity.adapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupSearch() {
        binding.apply {
            var searchJob: Job? = null

            etCommunitySearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    searchJob?.cancel()
                    searchJob = lifecycleScope.launch {
                        delay(300)
                        viewModel.searchCommunities(s?.toString() ?: "")
                    }
                }
            })

            btnSearch.setOnClickListener {
                val query = etCommunitySearch.text.toString()
                viewModel.searchCommunities(query)
                hideKeyboard()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.communitiesState.observe(this) { state ->
            when (state) {
                is CommunityResult.Success -> {
                    binding.progressBar?.visibility = View.GONE
                    adapter.submitList(state.communities)
                }
                is CommunityResult.Error -> {
                    binding.progressBar?.visibility = View.GONE
                    showError(state.message)
                }
                is CommunityResult.Loading -> {
                    binding.progressBar?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showCreateCommunityDialog() {
        val dialogBinding = DialogCreateCommunityBinding.inflate(layoutInflater)

        AlertDialog.Builder(this)
            .setTitle(R.string.create_new_community)
            .setView(dialogBinding.root)
            .setPositiveButton(R.string.create) { _, _ ->
                val name = dialogBinding.etCommunityName.text.toString().trim()
                val description = dialogBinding.etCommunityDescription.text.toString().trim()

                when {
                    name.isEmpty() -> {
                        showError(getString(R.string.error_empty_name))
                    }
                    name.length > Community.MAX_NAME_LENGTH -> {
                        showError(getString(R.string.error_name_too_long))
                    }
                    description.length > Community.MAX_DESCRIPTION_LENGTH -> {
                        showError(getString(R.string.error_description_too_long))
                    }
                    else -> {
                        createCommunity(name, description, dialogBinding)
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
            .show()
    }

    private fun createCommunity(
        name: String,
        description: String,
        dialogBinding: DialogCreateCommunityBinding
    ) {
        val newCommunity = Community(
            name = name,
            description = description,
            createdBy = "William8677",
            createdAt = Instant.parse("2025-01-05T00:36:50Z").toEpochMilli(),
            isPrivate = dialogBinding.switchPrivate?.isChecked ?: false,
            categories = dialogBinding.etCategories?.text?.toString()
                ?.split(",")
                ?.map { it.trim() }
                ?.filter { it.isNotEmpty() }
                ?.take(Community.MAX_CATEGORIES)
                ?: emptyList(),
            visibility = when {
                dialogBinding.switchPrivate?.isChecked == true -> Visibility.PRIVATE
                dialogBinding.switchRestricted?.isChecked == true -> Visibility.RESTRICTED
                else -> Visibility.PUBLIC
            }
        )
        viewModel.createCommunity(newCommunity)
    }

    private fun showCommunitySettings(community: Community) {
        // Implementar lógica para mostrar configuraciones de la comunidad
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }
}
