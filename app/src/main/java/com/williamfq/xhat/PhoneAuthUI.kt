// PhoneAuthUI.kt
package com.williamfq.xhat

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PhoneAuthUI(
    modifier: Modifier = Modifier,
    onSendCode: (String) -> Unit,
    onVerifyCode: (String) -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text("Número de Teléfono") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onSendCode(phoneNumber) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar Código")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = verificationCode,
            onValueChange = { verificationCode = it },
            label = { Text("Código de Verificación") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onVerifyCode(verificationCode) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verificar Código")
        }
    }
}
