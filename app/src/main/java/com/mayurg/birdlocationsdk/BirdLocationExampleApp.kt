package com.mayurg.birdlocationsdk

import android.app.Application
import com.mayurg.locationsdk.BirdLocationSDK

class BirdLocationExampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        BirdLocationSDK.initialize(this,BuildConfig.BirdLocationSDK_ApiKey,true)
    }

    override fun onTerminate() {
        super.onTerminate()
        BirdLocationSDK.destroy()
    }

}