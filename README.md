
# Location SDK

This SDK provides access to a location tracking of the device with simple integration. Checkout the sample usage in `:app` module

### Example Screenshot from the app

<a href="https://ibb.co/NNNpf8V"><img src="https://i.ibb.co/WzzF7bx/ss1.jpg" alt="ss1" border="0" width=200></a><br />


### Step 1:

Include the dependency in your module `build.gradle.kts`

    implementation("com.mayurgajra:locationsdk:1.0.0")

### Step 2:

Include `mavenCentral()` in your repositories block of `settings.gradle.kts` as following:

    dependencyResolutionManagement {  
      repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)  
        repositories {  
		    google()  
            mavenCentral()  // include this
        }  
    }

### Step 3:

Include following permissions into your `AndroidManifest.xml` & for target os > Android 6.0 please make sure to ask these permissions at runtime before accessing the library feature.

    <uses-permission android:name="android.permission.INTERNET" />  
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />  
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />


### Step 4:

To be able to use the features. First you must authenticate with your API_KEY in the Application class as following:

    class CustomApp : Application() {  
      
        override fun onCreate() {  
            super.onCreate()  
            BirdLocationSDK.initialize(context = this,"YOUR_API_KEY", enableLogging = true)  
        }  
      
        override fun onTerminate() {  
            super.onTerminate()  
            BirdLocationSDK.destroy()  
        }  
    }

### Step 5:

Include your CustomApp class in your `AndroidManifest.xml`  `name` field

    <application  
      android:name=".CustomApp"


### Step 6:

To request timely location update, you can call enableTimelyUpdates with interval in milliseconds & optionally listen to success & failure of each call with callbacks:

    BirdLocationSDK.enableTimelyUpdates(interval = 15000, { lat, long ->  
      viewModel.onLocationUpdate(lat, long)  
    }, { errorCode, errorMessage ->  
      viewModel.onLocationError(errorMessage, errorCode)  
    })

To request location update just once, you can call requestLocationUpdateOnce & optionally listen to success & failure with callbacks:

    BirdLocationSDK.requestLocationUpdateOnce({ lat, long ->  
      viewModel.onLocationUpdate(lat, long)  
    }, { errorCode, errorMessage ->  
      viewModel.onLocationError(errorMessage, errorCode)  
    })

