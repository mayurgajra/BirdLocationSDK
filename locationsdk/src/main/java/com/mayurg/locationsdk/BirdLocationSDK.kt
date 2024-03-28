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

/**
 * BirdLocationSDK is a class that provides location services.
 * It uses the CoroutineScope to handle asynchronous tasks.
 *
 * This class is a singleton, meaning there can only be one instance of it at any given time.
 * It is initialized with an API key and a context, and can optionally enable logging.
 *
 * It provides methods to request location updates either once or at regular intervals.
 * It also provides a method to destroy the instance when it is no longer needed.
 *
 * Example usage:
 *
 * 1) Initialize the SDK in Application class
 * BirdLocationSDK.initialize(context, "your_api_key", true)
 *
 * 2) Request a single location update
 * BirdLocationSDK.requestLocationUpdateOnce { lat, lon ->
 * println("Location updated: $lat, $lon")
 * }
 *
 * 3) Request location updates every 5 minutes
 * BirdLocationSDK.enableTimelyUpdates(5 * 60 * 1000) { lat, lon ->
 * println("Location updated: $lat, $lon")
 * }
 *
 * 4) When done, destroy the instance
 * BirdLocationSDK.destroy()
 *
 * */
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

        authPreferences = AuthPreferencesImpl(encryptedAuthPrefs)
        tokenRefresher = TokenRefresher(authPreferences)

        locationApi = RetrofitUtils.getRetrofit(okHttpClient).create(LocationApi::class.java)
        authApi = RetrofitUtils.getRetrofit(okHttpClient).create(AuthApi::class.java)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        defaultLocationClient = DefaultLocationClient(context, fusedLocationProviderClient)

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

        /** This function initializes the SDK with the provided context, API key, and logging option
         * It creates a new instance of the SDK. Must be called before any other function.
         *
         * throws IllegalStateException if the API key is blank or invalid
         */
        @Throws(IllegalStateException::class)
        fun initialize(context: Context, apiKey: String, enableLogging: Boolean = false) {
            instance = BirdLocationSDK().apply {
                initialize(context, apiKey, enableLogging)
            }
        }

        /**
         * This function enables location updates at regular intervals
         *
         *It takes an interval in milliseconds, and two optional callback functions
         *
         *The first callback function is called with the latitude and longitude each time the location is updated
         *The second callback function is called with an error code and message if an error occurs
         */
        fun enableTimelyUpdates(
            interval: Long,
            onLocationUpdated: (Double, Double) -> Unit = { _, _ -> },
            onError: (Int, String) -> Unit = { _, _ -> }
        ) {
            instance?.enableTimelyUpdates(interval, onLocationUpdated, onError)
        }


        /**
         * This function requests a single location update
         *
         * It takes two optional callback functions
         *
         * The first callback function is called with the latitude and longitude when the location is updated
         * The second callback function is called with an error code and message if an error occurs
         */
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