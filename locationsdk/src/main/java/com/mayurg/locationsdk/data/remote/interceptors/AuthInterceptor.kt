package com.mayurg.locationsdk.data.remote.interceptors

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

internal class AuthInterceptor(
    private val tokenRefresher: TokenRefresher
) : Interceptor {

    companion object {
        const val UNAUTHORIZED = 403
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        if (response.code == UNAUTHORIZED) {
            response.close()
            val newToken = runBlocking { tokenRefresher.refreshToken() }

            val newRequest: Request = originalRequest.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()

            return chain.proceed(newRequest)
        }

        return response
    }
}