package com.mayurg.locationsdk.data.remote.interceptors

import com.mayurg.locationsdk.data.remote.apis.LocationApi
import com.mayurg.locationsdk.domain.preferences.AuthPreferences
import java.io.IOException

internal class TokenRefresher(
    private val authPreferences: AuthPreferences,
    private val locationApi: LocationApi
) {
    suspend fun refreshToken(): String {
        val refreshToken = "Bearer ${authPreferences.loadRefreshToken()}"
        val refreshResponse = locationApi.refreshToken(refreshToken)

        if (refreshResponse.isSuccessful) {
            val newToken = refreshResponse.body()?.accessToken ?: ""
            val expiresAt = refreshResponse.body()?.expiresAt ?: ""
            authPreferences.saveAccessToken(newToken)
            authPreferences.saveExpiresAt(expiresAt)
            return newToken
        } else {
            throw IOException("Failed to refresh token")
        }
    }
}