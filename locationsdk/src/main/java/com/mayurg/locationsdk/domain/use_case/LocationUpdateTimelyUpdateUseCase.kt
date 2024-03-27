package com.mayurg.locationsdk.domain.use_case

import android.location.Location
import com.mayurg.locationsdk.domain.repository.LocationApiRepository
import com.mayurg.locationsdk.domain.repository.LocationClient
import com.mayurg.locationsdk.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal class LocationUpdateTimelyUpdateUseCase(
    private val locationClient: LocationClient,
    private val locationApiRepository: LocationApiRepository,
    private val scope: CoroutineScope
) {

    fun updateTimelyLocation(
        interval: Long,
        onLocationUpdated: (Double, Double) -> Unit,
        onError: (Int, String) -> Unit
    ) {
        locationClient.getLocationUpdates(interval)
            .onEach { location ->
                try {
                    handleUpdateLocationResult(location, onLocationUpdated, onError)
                } catch (e: Exception) {
                    onError(-1, e.message ?: "Unknown error")
                }
            }.launchIn(scope)
    }

    private fun handleUpdateLocationResult(
        location: Location,
        onLocationUpdated: (Double, Double) -> Unit,
        onError: (Int, String) -> Unit
    ) {
        scope.launch {
            val result = locationApiRepository.updateLocation(location)
            if (result is Result.Success) {
                onLocationUpdated(location.latitude, location.longitude)
            } else if (result is Result.Failure) {
                onError(result.errorCode, result.message)
            }
        }
    }
}