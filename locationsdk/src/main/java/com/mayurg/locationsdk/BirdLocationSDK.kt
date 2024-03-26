package com.mayurg.locationsdk

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mayurg.locationsdk.data.local.preferences.AuthPreferencesImpl
import com.mayurg.locationsdk.data.remote.AuthInterceptor
import com.mayurg.locationsdk.data.remote.LocationApi
import com.mayurg.locationsdk.data.repository.DefaultLocationClient
import com.mayurg.locationsdk.data.repository.LocationApiRepositoryImpl
import com.mayurg.locationsdk.domain.use_case.AuthUseCase
import com.mayurg.locationsdk.domain.use_case.LocationUpdateOnceUseCase
import com.mayurg.locationsdk.domain.use_case.LocationUpdateTimelyUpdateUseCase
import com.mayurg.locationsdk.utils.Result.Failure
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.coroutines.CoroutineContext

class BirdLocationSDK : CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job


    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().addInterceptor(AuthInterceptor(authPreferences)).build()
    }

    private val locationApi: LocationApi by lazy {
        Retrofit.Builder()
            .baseUrl(LocationApi.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(LocationApi::class.java)
    }

    private lateinit var authSharedPrefs: SharedPreferences
    private lateinit var authPreferences: AuthPreferencesImpl
    private lateinit var locationRepository: LocationApiRepositoryImpl
    private lateinit var authUseCase: AuthUseCase
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var defaultLocationClient: DefaultLocationClient
    private lateinit var locationUpdateTimelyUpdateUseCase: LocationUpdateTimelyUpdateUseCase
    private lateinit var locationUpdateOnceUseCase: LocationUpdateOnceUseCase

    @Throws(IllegalStateException::class)
    private fun initialize(context: Context, apiKey: String) {

        if (apiKey.isBlank()) {
            throw IllegalStateException("API Key cannot be blank")
        }


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        defaultLocationClient = DefaultLocationClient(context, fusedLocationProviderClient)
        authSharedPrefs = context.getSharedPreferences("auth_shared_prefs", Context.MODE_PRIVATE)
        authPreferences = AuthPreferencesImpl(authSharedPrefs)
        locationRepository = LocationApiRepositoryImpl(locationApi, authPreferences)
        authUseCase = AuthUseCase(locationRepository)
        locationUpdateTimelyUpdateUseCase =
            LocationUpdateTimelyUpdateUseCase(defaultLocationClient, locationRepository)
        locationUpdateOnceUseCase =
            LocationUpdateOnceUseCase(defaultLocationClient, locationRepository)


        launch {
            val authResult = authUseCase.auth(apiKey)
            if (authResult is Failure) {
                throw IllegalStateException("${authResult.errorCode}: ${authResult.message}")
            }
        }
    }

    private fun enableTimelyUpdates(
        interval: Long,
        onLocationUpdated: (Double, Double) -> Unit = { _, _ -> },
        onError: (Int, String) -> Unit = { _, _ -> }
    ) {
        launch {
            locationUpdateTimelyUpdateUseCase.updateTimelyLocation(
                this,
                interval,
                onLocationUpdated,
                onError
            )
        }
    }

    private fun requestLocationUpdateOnce(
        onLocationUpdated: (Double, Double) -> Unit = { _, _ -> },
        onError: (Int, String) -> Unit = { _, _ -> }
    ) {
        launch {
            locationUpdateOnceUseCase.updateLocationOnce(
                this,
                onLocationUpdated,
                onError
            )
        }
    }

    private fun destroy() {
        job.cancel()
    }

    companion object {
        private var instance: BirdLocationSDK? = null

        fun initialize(context: Context, apiKey: String) {
            instance = BirdLocationSDK().apply {
                initialize(context, apiKey)
            }
        }

        fun enableTimelyUpdates(
            interval: Long,
            onLocationUpdated: (Double, Double) -> Unit = { _, _ -> },
            onError: (Int, String) -> Unit = { _, _ -> }
        ) {
            instance?.enableTimelyUpdates(interval, onLocationUpdated, onError)
        }

        fun requestLocationUpdateOnce(
            onLocationUpdated: (Double, Double) -> Unit = { _, _ -> },
            onError: (Int, String) -> Unit = { _, _ -> }
        ) {
            instance?.requestLocationUpdateOnce(onLocationUpdated, onError)
        }

        fun destroy() {
            instance?.destroy()
            instance = null
        }
    }

}