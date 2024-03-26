package com.mayurg.locationsdk.data.remote

import com.mayurg.locationsdk.data.local.preferences.AuthPreferencesImpl
import com.mayurg.locationsdk.data.remote.dto.RefreshTokenResponseDto
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

internal class AuthInterceptor(
    private val authPreferences: AuthPreferencesImpl,
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val response = chain.proceed(originalRequest)

        if (response.code == 403) {
            response.close()

            val refreshToken = "Bearer ${authPreferences.loadRefreshToken()}"

             val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
                .let { MoshiConverterFactory.create(it) }

            val locationApi = Retrofit.Builder()
                .baseUrl(LocationApi.BASE_URL)
                .addConverterFactory(moshi)
                .build()
                .create(LocationApi::class.java)

            var refreshResponse: retrofit2.Response<RefreshTokenResponseDto>? = null

            runBlocking {
                launch {
                    refreshResponse = locationApi.refreshToken(refreshToken)
                }.join()
            }

            if (refreshResponse?.isSuccessful == true) {
                val newToken = refreshResponse?.body()?.accessToken ?: ""
                val expiresAt = refreshResponse?.body()?.expiresAt ?: ""
                authPreferences.saveAccessToken(newToken)
                authPreferences.saveExpiresAt(expiresAt)

                val newRequest: Request = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $newToken")
                    .build()

                return chain.proceed(newRequest)
            }
        }

        return response
    }
}