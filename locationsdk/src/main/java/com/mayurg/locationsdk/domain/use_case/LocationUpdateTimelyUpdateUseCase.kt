package com.mayurg.locationsdk.domain.use_case

import com.mayurg.locationsdk.domain.repository.LocationApiRepository
import com.mayurg.locationsdk.domain.repository.LocationClient
import com.mayurg.locationsdk.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class LocationUpdateTimelyUpdateUseCase(
    private val locationClient: LocationClient,
    private val locationApiRepository: LocationApiRepository,
) {

    fun updateTimelyLocation(
        scope: CoroutineScope,
        interval: Long,
        onLocationUpdated: (Double, Double) -> Unit,
        onError: (Int, String) -> Unit
    ) {
        locationClient.getLocationUpdates(interval)
            .onEach { location ->
                val result = locationApiRepository.updateLocation(location)
                if (result is Result.Success) {
                    onLocationUpdated(location.latitude, location.longitude)
                } else if (result is Result.Failure) {
                    onError(result.errorCode, result.message)
                }
            }.launchIn(scope)
    }

}