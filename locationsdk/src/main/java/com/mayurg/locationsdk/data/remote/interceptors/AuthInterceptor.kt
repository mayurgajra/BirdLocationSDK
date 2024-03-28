package com.mayurg.locationsdk.data.remote.interceptors

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/** This class is responsible for intercepting HTTP requests and responses.
* It checks if the response code is 403 (UNAUTHORIZED) and refreshes the token if needed.
*
* @param tokenRefresher: An instance of the TokenRefresher interface to refresh the token.
 */
internal class AuthInterceptor(
    private val tokenRefresher: TokenRefresher
) : Interceptor {

    companion object {
        // HTTP status code for unauthorized access.
        const val UNAUTHORIZED = 403
    }

    // This function intercepts the HTTP request and response.
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        // The original request is obtained from the chain.
        val originalRequest = chain.request()
        // The response is obtained by proceeding with the original request.
        val response = chain.proceed(originalRequest)

        // If the response code is UNAUTHORIZED (403), the token is refreshed.
        if (response.code == UNAUTHORIZED) {
            // Close the response to free resources.
            response.close()
            // Refresh the token by calling the refreshToken function of the tokenRefresher.
            val newToken = runBlocking { tokenRefresher.refreshToken() }

            // Build a new request with the refreshed token in the Authorization header.
            val newRequest: Request = originalRequest.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()

            // Proceed with the new request and return the response.
            return chain.proceed(newRequest)
        }

        // If the response code is not UNAUTHORIZED, return the original response.
        return response
    }
}