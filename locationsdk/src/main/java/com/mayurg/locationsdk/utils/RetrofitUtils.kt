package com.mayurg.locationsdk.utils

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal object RetrofitUtils {

    private const val BASE_URL = "https://dummy-api-mobile.api.sandbox.bird.one/"

    fun getRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
            .let { MoshiConverterFactory.create(it) }

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(moshi)
            .client(okHttpClient)
            .build()

    }

}