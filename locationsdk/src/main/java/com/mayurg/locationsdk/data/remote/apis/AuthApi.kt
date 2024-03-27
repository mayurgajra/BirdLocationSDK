package com.mayurg.locationsdk.data.remote.apis

import com.mayurg.locationsdk.data.remote.dto.AuthKeyResponseDto
import com.mayurg.locationsdk.data.remote.dto.RefreshTokenResponseDto
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

internal interface AuthApi {

    @POST(AuthApi.AUTH_ENDPOINT)
    suspend fun auth(
        @Header("Authorization") apiKey: String
    ): Response<AuthKeyResponseDto>

    @POST(REFRESH_ENDPOINT)
    suspend fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): Response<RefreshTokenResponseDto>

    companion object {
        const val AUTH_ENDPOINT = "/auth"
        const val REFRESH_ENDPOINT = "/auth/refresh"
    }
}