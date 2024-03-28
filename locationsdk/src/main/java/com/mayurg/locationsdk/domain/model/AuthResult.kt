package com.mayurg.locationsdk.domain.model

internal data class AuthResult(
    val accessToken: String,
    val expiresAt: String,
    val refreshToken: String
)
