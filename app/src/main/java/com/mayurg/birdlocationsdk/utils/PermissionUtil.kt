package com.mayurg.birdlocationsdk.utils

import android.Manifest
import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

object PermissionUtil {
    fun checkForLocationPermission(activity: Activity, permissionGranted: () -> Unit, locationPermissionRequest: ActivityResultLauncher<Array<String>>) {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            permissionGranted()
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }
}