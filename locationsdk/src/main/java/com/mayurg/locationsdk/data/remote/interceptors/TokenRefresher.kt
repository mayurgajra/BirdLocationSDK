package com.mayurg.locationsdk.data.remote.interceptors

import com.mayurg.locationsdk.data.remote.apis.AuthApi
import com.mayurg.locationsdk.domain.preferences.AuthPreferences
import com.mayurg.locationsdk.utils.RetrofitUtils
import okhttp3.OkHttpClient
import java.io.IOException

internal class TokenRefresher(
    private val authPreferences: AuthPreferences,
) {
    suspend fun refreshToken(): String {
        val refreshToken = "Bearer ${authPreferences.loadRefreshToken()}"

        val authApi = RetrofitUtils.getRetrofit(OkHttpClient()).create(AuthApi::class.java)

        val refreshResponse = authApi.refreshToken(refreshToken)

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