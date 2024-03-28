package com.mayurg.birdlocationsdk

import android.app.Application
import com.mayurg.locationsdk.BirdLocationSDK

class BirdLocationExampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        BirdLocationSDK.initialize(
            context = this,
            apiKey = BuildConfig.BirdLocationSDK_ApiKey,
            enableLogging = true
        )
    }

    override fun onTerminate() {
        super.onTerminate()
        BirdLocationSDK.destroy()
    }

}