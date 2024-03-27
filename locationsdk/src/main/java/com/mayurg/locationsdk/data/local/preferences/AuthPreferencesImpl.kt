package com.mayurg.locationsdk.data.local.preferences

import android.content.SharedPreferences
import com.mayurg.locationsdk.domain.preferences.AuthPreferences

internal class AuthPreferencesImpl(
    private val sharedPreferences: SharedPreferences
) : AuthPreferences {


    companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_EXPIRES_AT = "expires_at"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }

    override fun saveAccessToken(accessToken: String) {
        sharedPreferences.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .apply()
    }

    override fun saveExpiresAt(expiresAt: String) {
        sharedPreferences.edit()
            .putString(KEY_EXPIRES_AT, expiresAt)
            .apply()
    }

    override fun saveRefreshToken(refreshToken: String) {
        sharedPreferences.edit()
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    override fun loadAccessToken(): String {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, "") ?: ""
    }

    override fun loadExpiresAt(): String {
        return sharedPreferences.getString(KEY_EXPIRES_AT, "") ?: ""
    }

    override fun loadRefreshToken(): String {
        return sharedPreferences.getString(KEY_REFRESH_TOKEN, "") ?: ""
    }
}