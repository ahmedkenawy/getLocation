package com.keno.getlocation.utils

import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*


class LocationManager(
    private val context: Context,
    private val onLocationUpdate: (Location) -> Unit
) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 5 * 1000 // 5 seconds in milliseconds
        fastestInterval = 5 * 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY

    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let {
                onLocationUpdate.invoke(it)
            }
        }
    }


    fun startLocationUpdates() {
        if (PermissionUtils.checkLocationPermission(context)) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
