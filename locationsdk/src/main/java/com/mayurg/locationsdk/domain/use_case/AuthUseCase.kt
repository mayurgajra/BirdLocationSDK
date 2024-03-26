package com.mayurg.locationsdk.domain.use_case

import com.mayurg.locationsdk.domain.model.AuthResult
import com.mayurg.locationsdk.domain.repository.LocationApiRepository

internal class AuthUseCase(
    private val locationApiRepository: LocationApiRepository,
) {

    suspend fun auth(apiKey: String): Result<AuthResult> {
        return locationApiRepository.auth(apiKey)
    }

}