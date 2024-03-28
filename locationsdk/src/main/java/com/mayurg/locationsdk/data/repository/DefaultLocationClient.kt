package com.mayurg.locationsdk.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.mayurg.locationsdk.domain.repository.LocationClient
import com.mayurg.locationsdk.utils.hasLocationPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/**
 * This class is responsible for providing location updates.
 * It uses the FusedLocationProviderClient to request location updates.
 * The location updates are provided as a Flow of Pair of latitude and longitude.
 *
 * @param context: The application context.
 * @param client: An instance of FusedLocationProviderClient to request location updates.
 */
internal class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient
): LocationClient {

    /**
     * This function returns a Flow of Pair of latitude and longitude representing the location updates.
     * It first checks if the location permission is granted and if the GPS is enabled.
     * Then, it creates a LocationRequest and a LocationCallback.
     * The LocationCallback sends the location updates to the Flow.
     * The function requests location updates from the FusedLocationProviderClient with the LocationRequest and LocationCallback.
     * When the Flow is collected, the location updates are requested.
     * When the Flow is not collected anymore, the location updates are removed.
     *
     * @param interval: The desired interval for location updates, in milliseconds.
     * @return A Flow of Pair of latitude and longitude representing the location updates.
     * @throws LocationClient.LocationException If the location permission is not granted or the GPS is disabled.
     */
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Pair<Double,Double>> {
        return callbackFlow {
            if(!context.hasLocationPermission()) {
                throw LocationClient.LocationException("Missing location permission")
            }

            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if(!isGpsEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("GPS is disabled")
            }

            val request = createLocationRequest(interval)

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let { location ->
                        launch { send(Pair(location.latitude,location.longitude)) }
                    }
                }
            }

            client.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
            )

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }

    /**
     * This function creates a LocationRequest with the desired interval.
     * The fastest interval is also set to the desired interval.
     *
     * @param interval: The desired interval for location updates, in milliseconds.
     * @return A LocationRequest with the desired interval.
     */
    private fun createLocationRequest(interval: Long): LocationRequest {
        return LocationRequest.create()
            .setInterval(interval)
            .setFastestInterval(interval)
    }
}