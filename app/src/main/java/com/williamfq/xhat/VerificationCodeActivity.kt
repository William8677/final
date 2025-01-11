package com.williamfq.xhat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.williamfq.xhat.databinding.ActivityVerificationCodeBinding

class VerificationCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnVerifyCode.setOnClickListener {
            val code = binding.etVerificationCode.text.toString()
            if (code.isNotEmpty()) {
                // Lógica para verificar el código
            } else {
                // Muestra un mensaje de error
            }
        }
    }
}
