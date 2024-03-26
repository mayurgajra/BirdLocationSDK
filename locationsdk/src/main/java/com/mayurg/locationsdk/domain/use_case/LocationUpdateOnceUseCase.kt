package com.mayurg.locationsdk.domain.use_case

import com.mayurg.locationsdk.domain.repository.LocationApiRepository
import com.mayurg.locationsdk.domain.repository.LocationClient
import com.mayurg.locationsdk.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal class LocationUpdateOnceUseCase(
    private val locationClient: LocationClient,
    private val locationApiRepository: LocationApiRepository,
) {

    fun updateLocationOnce(
        scope: CoroutineScope,
        onLocationUpdated: (Double, Double) -> Unit,
        onError: (Int, String) -> Unit
    ) {
        scope.launch {
            locationClient.getLocationUpdates(1000L)
                .first().let { location ->
                    val result = locationApiRepository.updateLocation(location)
                    if (result is Result.Success) {
                        onLocationUpdated(location.latitude, location.longitude)
                    } else if (result is Result.Failure) {
                        onError(result.errorCode, result.message)
                    }
                }
        }
    }

}