package com.mayurg.locationsdk.domain.preferences

interface AuthPreferences {

    fun saveAccessToken(accessToken: String)

    fun saveExpiresAt(expiresAt: String)

    fun saveRefreshToken(refreshToken: String)

    fun loadAccessToken(): String

    fun loadExpiresAt(): String

    fun loadRefreshToken(): String

    companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_EXPIRES_AT = "expires_at"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}