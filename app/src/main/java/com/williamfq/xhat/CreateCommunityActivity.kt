package com.williamfq.xhat.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.williamfq.xhat.R
import com.williamfq.xhat.databinding.ActivityCreateCommunityBinding
import com.williamfq.xhat.viewmodel.CreateCommunityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.Instant

@AndroidEntryPoint
class CreateCommunityActivity : AppCompatActivity() {

    companion object {
        private const val CURRENT_USER = "William8677"
        private val CURRENT_TIME = Instant.parse("2025-01-05T02:21:18Z").toEpochMilli()
    }

    private lateinit var binding: ActivityCreateCommunityBinding
    private val viewModel: CreateCommunityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBindings()
        setupListeners()
        observeViewModel()
    }

    private fun setupBindings() {
        binding.apply {
            viewModel = this@CreateCommunityActivity.viewModel
            lifecycleOwner = this@CreateCommunityActivity
        }
    }

    private fun setupListeners() {
        binding.apply {
            btnCreateCommunity.setOnClickListener {
                lifecycleScope.launch {
                    handleCreateCommunity()
                }
            }

            topBar.setOnClickListener {
                finish()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.apply {
            errorMessage.observe(this@CreateCommunityActivity) { message ->
                message?.let { showToast(it) }
            }

            isLoading.observe(this@CreateCommunityActivity) { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                binding.btnCreateCommunity.isEnabled = !isLoading
            }
        }
    }

    private suspend fun handleCreateCommunity() {
        try {
            viewModel.createCommunity()?.let { community ->
                showToast(getString(R.string.community_created_successfully))
                setResult(RESULT_OK)
                finish()
            }
        } catch (e: Exception) {
            showToast(e.message ?: getString(R.string.error_creating_community))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.resetForm()
    }
}