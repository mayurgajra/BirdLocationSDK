package com.mayurg.locationsdk.data.remote.dto

import com.squareup.moshi.Json

internal data class LocationUpdateResponseDto(
    @Json(name = "message")
    val message: String
)
