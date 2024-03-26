package com.mayurg.birdlocationsdk.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    val state = mutableStateOf(MainState())


    fun onLocationUpdate(latitude: Double, longitude: Double) {
        state.value = state.value.copy(
            isSuccessful = true,
            latitude = latitude,
            longitude = longitude
        )
    }

    fun onLocationError(error: String, errorCode: Int) {
        state.value = state.value.copy(
            isSuccessful = false,
            error = error,
            errorCode = errorCode
        )
    }

}