package com.mayurg.locationsdk.data.remote.interceptors

import com.mayurg.locationsdk.data.remote.apis.AuthApi
import com.mayurg.locationsdk.domain.preferences.AuthPreferences
import com.mayurg.locationsdk.utils.RetrofitUtils
import okhttp3.OkHttpClient
import java.io.IOException

/**
 * This class is responsible for refreshing the access token when it expires.
 * It uses the refresh token stored in the AuthPreferences to get a new access token from the AuthApi.
 *
 * @param authPreferences: An instance of AuthPreferences to load the refresh token and save the new access token.
 */
internal class TokenRefresher(
    private val authPreferences: AuthPreferences,
) {
    /**
     * This function is responsible for refreshing the access token.
     * It first loads the refresh token from the AuthPreferences.
     * Then, it creates an instance of the AuthApi and calls the refreshToken function with the loaded refresh token.
     * If the response is successful, it saves the new access token and its expiry time in the AuthPreferences and returns the new token.
     * If the response is not successful, it throws an IOException.
     *
     * @return The new access token.
     * @throws IOException If the token refresh request is not successful.
     */
    suspend fun refreshToken(): String {
        // Load the refresh token from the AuthPreferences.
        val refreshToken = "Bearer ${authPreferences.loadRefreshToken()}"

        // Create an instance of the AuthApi.
        val authApi = RetrofitUtils.getRetrofit(OkHttpClient()).create(AuthApi::class.java)

        // Call the refreshToken function of the AuthApi with the loaded refresh token.
        val refreshResponse = authApi.refreshToken(refreshToken)

        // If the response is successful, save the new access token and its expiry time in the AuthPreferences and return the new token.
        // If the response is not successful, throw an IOException.
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