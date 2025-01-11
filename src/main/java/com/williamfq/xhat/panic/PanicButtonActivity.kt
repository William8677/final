package com.williamfq.xhat.panic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.williamfq.domain.location.LocationTracker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PanicButtonActivity : AppCompatActivity() {

    @Inject
    lateinit var locationTracker: LocationTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            locationTracker.getCurrentLocation().collect { location ->
                // Manejar la ubicación aquí
            }
        }
    }
}
