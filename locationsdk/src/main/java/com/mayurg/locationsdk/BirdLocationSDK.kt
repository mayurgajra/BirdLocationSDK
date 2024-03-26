package com.mayurg.locationsdk

import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mayurg.locationsdk.data.local.preferences.AuthPreferencesImpl
import com.mayurg.locationsdk.data.remote.LocationApi
import com.mayurg.locationsdk.data.repository.DefaultLocationClient
import com.mayurg.locationsdk.data.repository.LocationApiRepositoryImpl
import com.mayurg.locationsdk.domain.use_case.AuthUseCase
import com.mayurg.locationsdk.domain.use_case.LocationUpdateOnceUseCase
import com.mayurg.locationsdk.domain.use_case.LocationUpdateTimelyUpdateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.coroutines.CoroutineContext

class BirdLocationSDK : CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job


    private val locationApi: LocationApi = Retrofit.Builder()
        .baseUrl(LocationApi.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(LocationApi::class.java)

    private lateinit var authSharedPrefs: SharedPreferences
    private lateinit var authPreferences: AuthPreferencesImpl
    private lateinit var locationRepository: LocationApiRepositoryImpl
    private lateinit var authUseCase: AuthUseCase
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var defaultLocationClient: DefaultLocationClient
    private lateinit var locationUpdateTimelyUpdateUseCase: LocationUpdateTimelyUpdateUseCase
    private lateinit var locationUpdateOnceUseCase: LocationUpdateOnceUseCase

    private fun initialize(context: Context, apiKey: String) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        defaultLocationClient = DefaultLocationClient(context, fusedLocationProviderClient)
        authSharedPrefs = context.getSharedPreferences("auth_shared_prefs", Context.MODE_PRIVATE)
        authPreferences = AuthPreferencesImpl(authSharedPrefs)
        locationRepository = LocationApiRepositoryImpl(locationApi, authPreferences)
        authUseCase = AuthUseCase(locationRepository)
        locationUpdateTimelyUpdateUseCase = LocationUpdateTimelyUpdateUseCase(defaultLocationClient, locationRepository)
        locationUpdateOnceUseCase = LocationUpdateOnceUseCase(defaultLocationClient, locationRepository)


        launch {
            val authResult = authUseCase.auth(apiKey)
            if (authResult.isSuccess) {

            } else {

            }
        }
    }

    private fun enableTimelyUpdates(interval: Long) {
        launch {
            locationUpdateTimelyUpdateUseCase.updateTimelyLocation(this,interval)
        }
    }

    private fun requestLocationUpdateOnce() {
        launch {
            locationUpdateOnceUseCase.updateLocationOnce(this)
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

        fun enableTimelyUpdates(interval: Long) {
            instance?.enableTimelyUpdates(interval)
        }

        fun requestLocationUpdateOnce() {
            instance?.requestLocationUpdateOnce()
        }

        fun destroy() {
            instance?.destroy()
            instance = null
        }
    }

}