package com.mayurg.locationsdk.domain.repository

import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(interval: Long):  Flow<Pair<Double,Double>>

    class LocationException(message: String): Exception()
}