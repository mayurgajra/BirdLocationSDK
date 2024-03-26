package com.mayurg.locationsdk.domain.use_case

import com.mayurg.locationsdk.domain.repository.LocationApiRepository
import com.mayurg.locationsdk.domain.repository.LocationClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class LocationUpdateTimelyUpdateUseCase(
    private val locationClient: LocationClient,
    private val locationApiRepository: LocationApiRepository,
) {

    fun updateTimelyLocation(scope: CoroutineScope, interval: Long) {
        locationClient.getLocationUpdates(interval)
            .onEach { location ->
                locationApiRepository.updateLocation(location)
            }.launchIn(scope)
    }

}