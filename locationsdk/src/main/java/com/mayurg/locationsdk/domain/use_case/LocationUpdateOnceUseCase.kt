package com.mayurg.locationsdk.domain.use_case

import com.mayurg.locationsdk.domain.repository.LocationApiRepository
import com.mayurg.locationsdk.domain.repository.LocationClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal class LocationUpdateOnceUseCase(
    private val locationClient: LocationClient,
    private val locationApiRepository: LocationApiRepository,
) {

    fun updateLocationOnce(scope: CoroutineScope) {
        scope.launch {
            locationClient.getLocationUpdates(1000L)
                .first().let { location ->
                    locationApiRepository.updateLocation(location)
                }
        }
    }

}