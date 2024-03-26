package com.mayurg.locationsdk.data.local.preferences

import android.content.SharedPreferences
import com.mayurg.locationsdk.domain.preferences.AuthPreferences

class AuthPreferencesImpl(
    private val sharedPreferences: SharedPreferences
) : AuthPreferences {
    override fun saveAccessToken(accessToken: String) {
        sharedPreferences.edit()
            .putString(AuthPreferences.KEY_ACCESS_TOKEN, accessToken)
            .apply()
    }

    override fun saveExpiresAt(expiresAt: String) {
        sharedPreferences.edit()
            .putString(AuthPreferences.KEY_EXPIRES_AT, expiresAt)
            .apply()
    }

    override fun saveRefreshToken(refreshToken: String) {
        sharedPreferences.edit()
            .putString(AuthPreferences.KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    override fun loadAccessToken(): String {
       return sharedPreferences.getString(AuthPreferences.KEY_ACCESS_TOKEN, "") ?: ""
    }

    override fun loadExpiresAt(): String {
       return sharedPreferences.getString(AuthPreferences.KEY_EXPIRES_AT, "") ?: ""
    }

    override fun loadRefreshToken(): String {
       return sharedPreferences.getString(AuthPreferences.KEY_REFRESH_TOKEN, "") ?: ""
    }
}