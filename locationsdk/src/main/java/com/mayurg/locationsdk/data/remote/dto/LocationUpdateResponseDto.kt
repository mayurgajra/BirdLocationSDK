package com.mayurg.locationsdk.data.remote.dto

import com.squareup.moshi.Json

data class LocationUpdateResponseDto(
    @Json(name = "message")
    val message: String
)
