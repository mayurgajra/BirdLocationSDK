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
    private val scope: CoroutineScope
) {

    fun updateLocationOnce(
        onLocationUpdated: (Double, Double) -> Unit,
        onError: (Int, String) -> Unit
    ) {
        scope.launch {
            try {
                locationClient.getLocationUpdates(1000L)
                    .first().let { location ->
                        when (val result = locationApiRepository.updateLocation(location)) {
                            is Result.Success -> {
                                onLocationUpdated(location.latitude, location.longitude)
                            }
                            is Result.Failure -> {
                                onError(result.errorCode, result.message)
                            }
                            else -> {
                                onError(-1, "Unknown error occurred")
                            }
                        }
                    }
            } catch (e: Exception) {
                onError(-1, e.message ?: "Unknown error occurred")
            }
        }
    }

}