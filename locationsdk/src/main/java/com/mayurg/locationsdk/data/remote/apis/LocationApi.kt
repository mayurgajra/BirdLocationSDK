package com.mayurg.locationsdk.data.remote.apis

import com.mayurg.locationsdk.data.remote.dto.LocationUpdateResponseDto
import com.mayurg.locationsdk.data.remote.request.LocationUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

internal interface LocationApi {

    @POST(LOCATION_UPDATE_ENDPOINT)
    suspend fun locationUpdate(
        @Header("Authorization") accessToken: String,
        @Body locationUpdateRequest: LocationUpdateRequest
    ): Response<LocationUpdateResponseDto>

    companion object {
        const val LOCATION_UPDATE_ENDPOINT = "/location"
    }
}