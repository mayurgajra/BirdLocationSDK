package com.mayurg.locationsdk.data.remote

import com.mayurg.locationsdk.data.remote.dto.AuthKeyResponseDto
import com.mayurg.locationsdk.data.remote.dto.LocationUpdateResponseDto
import com.mayurg.locationsdk.data.remote.request.LocationUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

internal interface LocationApi {

    @POST("/auth")
    suspend fun auth(
        @Header("Authorization") apiKey: String
    ): Response<AuthKeyResponseDto>

    @POST("/location")
    suspend fun locationUpdate(
        @Header("Authorization") accessToken: String,
        @Body locationUpdateRequest: LocationUpdateRequest
    ): Response<LocationUpdateResponseDto>

    companion object {
        const val BASE_URL = "https://dummy-api-mobile.api.sandbox.bird.one/"
    }
}