package com.williamfq.xhat

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.williamfq.xhat.databinding.ActivityEmailRecoveryBinding

class EmailRecoveryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEmailRecoveryBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSendPhoneCode.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                // Lógica para enviar el código de verificación del teléfono
                Toast.makeText(this, "Código de verificación enviado al teléfono $phoneNumber", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Por favor, ingresa un número de teléfono", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSendEmailCode.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                sendRecoveryEmail(email)
            } else {
                Toast.makeText(this, "Por favor, ingresa un correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendRecoveryEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo de recuperación enviado a $email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al enviar el correo de recuperación", Toast.LENGTH_SHORT).show()
                }
            }
    }
}