package com.mayurg.birdlocationsdk.ui

data class MainState(
    val isSuccessful: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val error: String = "",
    val errorCode: Int = 0,
)
