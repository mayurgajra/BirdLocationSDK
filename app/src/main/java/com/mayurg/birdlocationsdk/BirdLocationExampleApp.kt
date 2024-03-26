package com.mayurg.birdlocationsdk

import android.app.Application
import com.mayurg.locationsdk.BirdLocationSDK

class BirdLocationExampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        BirdLocationSDK.initialize(this,"xdk8ih3kvw2c66isndihzke5")
    }

    override fun onTerminate() {
        super.onTerminate()
        BirdLocationSDK.destroy()
    }

}