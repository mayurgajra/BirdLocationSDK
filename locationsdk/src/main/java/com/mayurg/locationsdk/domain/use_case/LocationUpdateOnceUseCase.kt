package com.mayurg.locationsdk.domain.use_case

import com.mayurg.locationsdk.domain.repository.LocationApiRepository
import com.mayurg.locationsdk.domain.repository.LocationClient
import com.mayurg.locationsdk.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * This class is responsible for handling the use case of updating the location once.
 * It uses the LocationClient to get the location updates and the LocationApiRepository to update the location.
 * The location update is performed in the CoroutineScope provided.
 *
 * @param locationClient: An instance of LocationClient to get the location updates.
 * @param locationApiRepository: An instance of LocationApiRepository to update the location.
 * @param scope: The CoroutineScope in which the location update is performed.
 */
internal class LocationUpdateOnceUseCase(
    private val locationClient: LocationClient,
    private val locationApiRepository: LocationApiRepository,
    private val scope: CoroutineScope
) {

    /**
     * This function updates the location once.
     * It first gets the location updates from the LocationClient.
     * Then, it updates the location with the LocationApiRepository.
     * If the location update is successful, it calls the onLocationUpdated function with the latitude and longitude.
     * If the location update fails, it calls the onError function with the error code and message.
     * If an exception occurs, it calls the onError function with -1 as the error code and the exception message as the error message.
     *
     * @param onLocationUpdated: A function to call when the location is updated. It takes the latitude and longitude as parameters.
     * @param onError: A function to call when an error occurs. It takes the error code and message as parameters.
     */
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
                                onLocationUpdated(location.first, location.second)
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