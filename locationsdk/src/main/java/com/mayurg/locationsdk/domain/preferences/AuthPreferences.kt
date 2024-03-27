package com.mayurg.locationsdk.domain.preferences

internal interface AuthPreferences {

    fun saveAccessToken(accessToken: String)

    fun saveExpiresAt(expiresAt: String)

    fun saveRefreshToken(refreshToken: String)

    fun loadAccessToken(): String

    fun loadExpiresAt(): String

    fun loadRefreshToken(): String


}