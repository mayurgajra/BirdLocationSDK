package com.mayurg.locationsdk.domain.use_case

import com.mayurg.locationsdk.domain.model.AuthResult
import com.mayurg.locationsdk.domain.repository.LocationApiRepository
import com.mayurg.locationsdk.utils.Result

/**
 * This class is responsible for handling the authentication use case.
 * It uses the LocationApiRepository to authenticate with the provided API key.
 *
 * @param locationApiRepository: An instance of LocationApiRepository to perform the authentication.
 */
internal class AuthUseCase(
    private val locationApiRepository: LocationApiRepository,
) {

    /**
     * This function performs the authentication with the provided API key.
     * It delegates the authentication to the LocationApiRepository.
     *
     * @param apiKey: The API key to use for the authentication.
     * @return A Result of AuthResult representing the result of the authentication.
     */
    suspend fun auth(apiKey: String): Result<AuthResult> {
        return locationApiRepository.auth(apiKey)
    }

}