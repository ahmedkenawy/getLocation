package com.keno.getlocation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.keno.getlocation.ui.adapter.CurrentLocationAdapter
import com.keno.getlocation.R
import com.keno.getlocation.api.LocationResponse
import com.keno.getlocation.api.RetrofitInstance
import com.keno.getlocation.databinding.ActivityMainBinding
import com.keno.getlocation.model.CurrentLocation
import com.keno.getlocation.utils.PermissionUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    companion object {
        val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    private lateinit var binding: ActivityMainBinding

    val locations = ArrayList<CurrentLocation>()
    lateinit var locationAdapter: CurrentLocationAdapter
    private var isListenToLocation: Boolean = true
    val apiService = RetrofitInstance.apiService


    private val locationManager by lazy {
        com.keno.getlocation.utils.LocationManager(
            this
        ) { location ->
            val currentLocation = currentLocation(location)
            sendLocationToDatabase(currentLocation)

        }
    }

    private fun currentLocation(location: Location): CurrentLocation {
        val currentLocation = CurrentLocation(
            location.latitude.toString(),
            location.longitude.toString()
        )
        locations.add(
            currentLocation
        )
        locationAdapter.setData(locations)
        return currentLocation
    }

    private fun sendLocationToDatabase(currentLocation: CurrentLocation) {
        val call: Call<LocationResponse> = apiService.postData(currentLocation)
        call.enqueue(object : Callback<LocationResponse> {
            override fun onResponse(
                call: Call<LocationResponse>,
                response: Response<LocationResponse>
            ) {
                if (response.isSuccessful) {
                    // Handle the successful response
                    val result: LocationResponse? = response.body()
                    Log.d("TAG", "onResponse: ${result.toString()}")
                    Log.d("TAG", "onResponse: ${response.code()}")
                    // Do something with the result
                } else {
                    // Handle the error
                    // You can get more information about the error from response.errorBody()
                }
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                // Handle the failure
                t.printStackTrace()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        requestLocationPermission()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        callbacks()

    }

    private fun callbacks() {
        locationAdapter = CurrentLocationAdapter(locations)
        binding.myCustomRecyclerView.adapter = locationAdapter
        binding.fab.setOnClickListener {
            if (isListenToLocation) {
                isListenToLocation = false
                stopLocationUpdates()
                binding.fab.setImageResource(R.drawable.ic_play)
            } else {
                isListenToLocation = true
                startLocationUpdates()
                binding.fab.setImageResource(R.drawable.ic_stop)
            }
        }
    }

    private fun requestLocationPermission() {
        if (!PermissionUtils.checkLocationPermission(this)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE

            )

        } else {
            if (!isLocationEnabled()) {
                // If not enabled, show a dialog to enable location services
                showEnableLocationDialog()
            } else {
                startLocationUpdates()
            }
            // If permission is already granted, start location updates
        }
    }

    private fun startLocationUpdates() {
        locationManager.startLocationUpdates()
    }

    private fun stopLocationUpdates() {
        locationManager.stopLocationUpdates()
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, start location updates
                    startLocationUpdates()
                } else {
                    // Permission denied, handle accordingly (e.g., show a message)


                }
            }
            // Handle other permission requests if needed
            // ...
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun showEnableLocationDialog() {
        // Build an alert dialog to prompt the user to enable location services
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Location services are disabled. Do you want to enable them?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                // Open the location settings
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
                // Handle the case when the user chooses not to enable location services
            }

        val alert = dialogBuilder.create()
        alert.show()
    }
}