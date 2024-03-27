package com.mayurg.locationsdk.data.repository

import com.mayurg.locationsdk.data.remote.apis.AuthApi
import com.mayurg.locationsdk.data.remote.apis.LocationApi
import com.mayurg.locationsdk.data.remote.request.LocationUpdateRequest
import com.mayurg.locationsdk.domain.model.AuthResult
import com.mayurg.locationsdk.domain.model.LocationUpdateResult
import com.mayurg.locationsdk.domain.preferences.AuthPreferences
import com.mayurg.locationsdk.domain.repository.LocationApiRepository
import com.mayurg.locationsdk.utils.Result
import com.mayurg.locationsdk.utils.Result.Failure
import com.mayurg.locationsdk.utils.Result.Success
import timber.log.Timber


/**
 * `LocationApiRepositoryImpl` is a class that handles the operations related to user authentication and location updates.
 *
 * @param authApi: An instance of `AuthApi` to make authentication requests.
 * @param locationApi: An instance of `LocationApi` to make location update requests.
 * @param authPreferences: An instance of `AuthPreferences` to store and manage the user's authentication tokens.
 */
internal class LocationApiRepositoryImpl(
    private val authApi: AuthApi,
    private val locationApi: LocationApi,
    private val authPreferences: AuthPreferences
) : LocationApiRepository {


    /**
     * This function is used to authenticate the user.
     * It sends the user's API key to the server and receives an authentication result.
     * If the authentication is successful, it saves the authentication tokens.
     * If the authentication is not successful, it returns an error message.
     *
     * @param apiKey The user's API key.
     * @return The result of the authentication request.
     */
    override suspend fun auth(apiKey: String): Result<AuthResult> {
        val result = authApi.auth("Bearer $apiKey")


        Timber.tag("LocationRepositoryImpl").d("auth: $result")

        if (!result.isSuccessful) {
            val errorMessage = result.errorBody()?.string() ?: "Failed to authenticate"
            return Failure(result.code(), errorMessage)
        }

        val authResult = result.body() ?: return Failure(result.code(),"Failed to authenticate")

        authPreferences.saveAccessToken(authResult.accessToken)
        authPreferences.saveRefreshToken(authResult.refreshToken)
        authPreferences.saveExpiresAt(authResult.expiresAt)

        return Success(
            AuthResult(
                accessToken = authResult.accessToken,
                expiresAt = authResult.expiresAt,
                refreshToken = authResult.refreshToken
            )
        )
    }

    /**
     * This function is used to update the user's location.
     * It sends the user's latitude and longitude to the server and receives a location update result.
     * If the location update is successful, it returns a success message.
     * If the location update is not successful, it returns an error message.
     *
     * @param location The user's latitude and longitude.
     * @return The result of the location update request.
     */
    override suspend fun updateLocation(location: Pair<Double, Double>): Result<LocationUpdateResult> {
        val result = locationApi.locationUpdate(
            "Bearer ${authPreferences.loadAccessToken()}",
            LocationUpdateRequest(
                latitude = location.first,
                longitude = location.second
            )
        )

        Timber.tag("LocationRepositoryImpl").d("updateLocation: $result")

        if (!result.isSuccessful) {
            return Failure(result.code(),"Failed to update location")
        }

        val authResult =
            result.body() ?: return Failure(result.code(),"Failed to update location")

        return Success(
            LocationUpdateResult(
                message = authResult.message,
            )
        )
    }
}