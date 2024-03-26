package com.mayurg.locationsdk.domain.repository

import android.location.Location
import com.mayurg.locationsdk.domain.model.AuthResult
import com.mayurg.locationsdk.domain.model.LocationUpdateResult
import com.mayurg.locationsdk.utils.Result

internal interface LocationApiRepository {

     suspend fun auth(apiKey: String): Result<AuthResult>

     suspend fun updateLocation(location: Location) : Result<LocationUpdateResult>

}