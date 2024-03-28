package com.mayurg.locationsdk.data.remote.dto

import com.squareup.moshi.Json

internal data class RefreshTokenResponseDto(
    @Json(name = "accessToken")
    val accessToken: String,

    @Json(name = "expiresAt")
    val expiresAt: String,
)