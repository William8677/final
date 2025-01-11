package com.williamfq.xhat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.williamfq.domain.models.Location
import com.williamfq.domain.usecases.SendPanicAlertUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PanicViewModel @Inject constructor(
    private val sendPanicAlertUseCase: SendPanicAlertUseCase
) : ViewModel() {

    fun sendAlert(message: String, userId: String, location: Location?) {
        viewModelScope.launch {
            sendPanicAlertUseCase.invoke(
                message = message,
                userId = userId,
                location = location
            )
        }
    }
}
