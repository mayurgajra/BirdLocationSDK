package com.mayurg.locationsdk.data.repository

import android.location.Location
import android.util.Log
import com.mayurg.locationsdk.data.remote.LocationApi
import com.mayurg.locationsdk.data.remote.request.LocationUpdateRequest
import com.mayurg.locationsdk.domain.model.AuthResult
import com.mayurg.locationsdk.domain.model.LocationUpdateResult
import com.mayurg.locationsdk.domain.preferences.AuthPreferences
import com.mayurg.locationsdk.domain.repository.LocationApiRepository

internal class LocationApiRepositoryImpl(
    private val locationApi: LocationApi,
    private val authPreferences: AuthPreferences
) : LocationApiRepository {
    override suspend fun auth(apiKey: String): Result<AuthResult> {
        val result = locationApi.auth("Bearer $apiKey")


        Log.d("LocationRepositoryImpl", "auth: $result")

        if (!result.isSuccessful) {
            return Result.failure(Exception("Failed to authenticate"))
        }

        val authResult = result.body() ?: return Result.failure(Exception("Failed to authenticate"))

        authPreferences.saveAccessToken(authResult.accessToken)
        authPreferences.saveRefreshToken(authResult.refreshToken)
        authPreferences.saveExpiresAt(authResult.expiresAt)

        return Result.success(
            AuthResult(
                accessToken = authResult.accessToken,
                expiresAt = authResult.expiresAt,
                refreshToken = authResult.refreshToken
            )
        )
    }

    override suspend fun updateLocation(location: Location): Result<LocationUpdateResult> {
        val result = locationApi.locationUpdate(
            "Bearer ${authPreferences.loadAccessToken()}",
            LocationUpdateRequest(
                latitude = location.latitude,
                longitude = location.longitude
            )
        )

        Log.d("LocationRepositoryImpl", "updateLocation: $result")

        if (!result.isSuccessful) {
            return Result.failure(Exception("Failed to update location"))
        }

        val authResult =
            result.body() ?: return Result.failure(Exception("Failed to update location"))

        return Result.success(
            LocationUpdateResult(
                message = authResult.message,
            )
        )
    }
}