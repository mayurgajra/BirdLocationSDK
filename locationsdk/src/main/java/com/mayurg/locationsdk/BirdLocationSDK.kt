package com.mayurg.locationsdk

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mayurg.locationsdk.data.local.preferences.AuthPreferencesImpl
import com.mayurg.locationsdk.data.remote.apis.AuthApi
import com.mayurg.locationsdk.data.remote.apis.LocationApi
import com.mayurg.locationsdk.data.remote.interceptors.AuthInterceptor
import com.mayurg.locationsdk.data.remote.interceptors.TokenRefresher
import com.mayurg.locationsdk.data.repository.DefaultLocationClient
import com.mayurg.locationsdk.data.repository.LocationApiRepositoryImpl
import com.mayurg.locationsdk.domain.use_case.AuthUseCase
import com.mayurg.locationsdk.domain.use_case.LocationUpdateOnceUseCase
import com.mayurg.locationsdk.domain.use_case.LocationUpdateTimelyUpdateUseCase
import com.mayurg.locationsdk.utils.Result.Failure
import com.mayurg.locationsdk.utils.RetrofitUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class BirdLocationSDK : CoroutineScope {

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job


    private lateinit var tokenRefresher: TokenRefresher

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().addInterceptor(AuthInterceptor(tokenRefresher)).build()
    }


    private lateinit var locationApi: LocationApi
    private lateinit var authApi: AuthApi

    private lateinit var masterKey: MasterKey

    private lateinit var encryptedAuthPrefs: SharedPreferences

    private lateinit var authPreferences: AuthPreferencesImpl
    private lateinit var locationRepository: LocationApiRepositoryImpl
    private lateinit var authUseCase: AuthUseCase
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var defaultLocationClient: DefaultLocationClient
    private lateinit var locationUpdateTimelyUpdateUseCase: LocationUpdateTimelyUpdateUseCase
    private lateinit var locationUpdateOnceUseCase: LocationUpdateOnceUseCase

    @Throws(IllegalStateException::class)
    private fun initialize(context: Context, apiKey: String, enableLogging: Boolean = false) {

        if (apiKey.isBlank()) {
            throw IllegalStateException("API Key cannot be blank")
        }

        if (enableLogging) {
            Timber.plant(Timber.DebugTree())
        }


        masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        encryptedAuthPrefs = EncryptedSharedPreferences.create(
            context,
            "auth_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        locationApi = RetrofitUtils.getRetrofit(okHttpClient).create(LocationApi::class.java)
        authApi = RetrofitUtils.getRetrofit(okHttpClient).create(AuthApi::class.java)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        defaultLocationClient = DefaultLocationClient(context, fusedLocationProviderClient)
        authPreferences = AuthPreferencesImpl(encryptedAuthPrefs)
        tokenRefresher = TokenRefresher(authPreferences, locationApi)
        locationRepository = LocationApiRepositoryImpl(authApi, locationApi, authPreferences)
        authUseCase = AuthUseCase(locationRepository)
        locationUpdateTimelyUpdateUseCase =
            LocationUpdateTimelyUpdateUseCase(defaultLocationClient, locationRepository, this)
        locationUpdateOnceUseCase =
            LocationUpdateOnceUseCase(defaultLocationClient, locationRepository, this)


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

        @Throws(IllegalStateException::class)
        fun initialize(context: Context, apiKey: String, enableLogging: Boolean = false) {
            instance = BirdLocationSDK().apply {
                initialize(context, apiKey, enableLogging)
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