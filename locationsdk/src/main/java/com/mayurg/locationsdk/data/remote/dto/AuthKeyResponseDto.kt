package com.mayurg.locationsdk.data.remote.dto

import com.squareup.moshi.Json

internal data class AuthKeyResponseDto(
    @Json(name = "accessToken")
    val accessToken: String,

    @Json(name = "expiresAt")
    val expiresAt: String,

    @Json(name = "refreshToken")
    val refreshToken: String
)
