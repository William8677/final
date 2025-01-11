package com.williamfq.xhat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.williamfq.xhat.databinding.ActivityPhoneNumberBinding
import java.util.concurrent.TimeUnit

class PhoneNumberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneNumberBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSendPhoneCode.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            val email = binding.etEmail.text.toString().trim() // Campo opcional para el correo

            if (validatePhoneNumber(phoneNumber)) {
                sendVerificationCode(phoneNumber, email)
            } else {
                Toast.makeText(this, R.string.enter_valid_number, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSendEmailCode.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                sendRecoveryEmail(email)
            } else {
                Toast.makeText(this, "Please enter an email", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnForgotAccount.setOnClickListener {
            val intent = Intent(this, EmailRecoveryActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        // Validación básica para asegurarse de que el número no esté vacío y tenga un formato correcto
        return phoneNumber.isNotEmpty() && phoneNumber.length >= 10
    }

    private fun sendVerificationCode(phoneNumber: String, email: String?) {
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Manejar la verificación automática completada
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@PhoneNumberActivity, R.string.verification_failed, Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    val intent = Intent(this@PhoneNumberActivity, VerificationCodeActivity::class.java)
                    intent.putExtra("verificationId", verificationId)
                    intent.putExtra("userEmail", email) // Pasar el correo a la siguiente actividad
                    startActivity(intent)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun sendRecoveryEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Recovery email sent to $email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send recovery email", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
