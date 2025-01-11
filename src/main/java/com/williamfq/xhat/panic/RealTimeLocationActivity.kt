package com.williamfq.xhat.panic

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.williamfq.xhat.databinding.ActivityRealTimeLocationBinding
import com.williamfq.domain.location.LocationTracker
import com.williamfq.xhat.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RealTimeLocationActivity : AppCompatActivity() {

    @Inject
    lateinit var locationTracker: LocationTracker

    private lateinit var binding: ActivityRealTimeLocationBinding
    private lateinit var mapFragment: SupportMapFragment
    private var googleMap: GoogleMap? = null
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRealTimeLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        setupUI()
        checkPermissionsWithRationale()
    }

    private fun setupUI() {
        // Configurar el botón de ubicación actual
        binding.fabCurrentLocation.setOnClickListener {
            currentLocation?.let { location ->
                animateToLocation(location)
            } ?: Toast.makeText(
                this,
                getString(R.string.waiting_for_location),
                Toast.LENGTH_SHORT
            ).show()
        }

        // Mostrar el indicador de carga inicial
        showLoading(true)
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.tvLocation.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun checkPermissionsWithRationale() {
        val permissions = getRequiredPermissions()

        if (shouldShowPermissionRationale(permissions)) {
            showPermissionRationaleDialog()
        } else {
            checkPermissions()
        }
    }

    private fun getRequiredPermissions(): List<String> {
        return mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
    }

    private fun shouldShowPermissionRationale(permissions: List<String>): Boolean {
        return permissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(this, it)
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.permission_required))
            .setMessage(getString(R.string.location_permission_rationale))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                checkPermissions()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                showPermissionDeniedMessage()
            }
            .create()
            .show()
    }

    private fun initializeMap() {
        mapFragment.getMapAsync { map ->
            googleMap = map
            setupMap()
            startLocationUpdates()
        }
    }

    private fun setupMap() {
        googleMap?.apply {
            if (hasLocationPermission()) {
                if (ActivityCompat.checkSelfPermission(
                        this@RealTimeLocationActivity, // Cambio aquí
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@RealTimeLocationActivity, // Cambio aquí
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                isMyLocationEnabled = true
                uiSettings.apply {
                    isZoomControlsEnabled = true
                    isCompassEnabled = true
                    isMyLocationButtonEnabled = true
                }
            }
        }
    }

    private fun startLocationUpdates() {
        lifecycleScope.launch {
            locationTracker.getCurrentLocation()
                .catch { exception ->
                    handleLocationError(exception)
                }
                .collectLatest { location ->
                    handleLocation(location)
                }
        }
    }

    private fun handleLocation(location: Location?) {
        showLoading(false)
        location?.let { validLocation ->
            currentLocation = validLocation
            updateLocationUI(validLocation)
            animateToLocation(validLocation)
        } ?: showLocationError()
    }

    private fun updateLocationUI(location: Location) {
        binding.tvLocation.text = getString(
            R.string.location_format,
            location.latitude,
            location.longitude
        )
    }

    private fun animateToLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, 15f)
        )
    }

    private fun handleLocationError(exception: Throwable) {
        showLoading(false)
        Toast.makeText(
            this,
            getString(R.string.location_error_with_message, exception.localizedMessage),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLocationError() {
        binding.tvLocation.text = getString(R.string.location_not_available)
        Toast.makeText(
            this,
            getString(R.string.location_error),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissions() {
        val permissions = getRequiredPermissions()
        val permissionsToRequest = permissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(
                permissionsToRequest.toTypedArray(),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            initializeMap()
        }
    }

    private fun showPermissionDeniedMessage() {
        Toast.makeText(
            this,
            getString(R.string.permissions_required_message),
            Toast.LENGTH_LONG
        ).show()
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this, getString(R.string.permissions_granted), Toast.LENGTH_SHORT).show()
                    initializeMap()
                } else {
                    showPermissionDeniedMessage()
                }
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
