package com.mayurg.birdlocationsdk.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mayurg.birdlocationsdk.ui.theme.BirdLocationSDKTheme
import com.mayurg.locationsdk.BirdLocationSDK

class MainActivity : ComponentActivity() {

    companion object {
        private const val BUTTON_ID_ONCE = "once"
        private const val BUTTON_ID_TIMELY_UPDATE = "timely_update"
    }

    private var clickedButtonId = BUTTON_ID_TIMELY_UPDATE


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.get(Manifest.permission.ACCESS_FINE_LOCATION) ?: false -> {
                when (clickedButtonId) {
                    BUTTON_ID_TIMELY_UPDATE -> enableBirdLocationUpdates()
                    BUTTON_ID_ONCE -> requestLocationOnce()
                }
            }

            permissions.get(Manifest.permission.ACCESS_COARSE_LOCATION) ?: false -> {
                // Only approximate location access granted.
            }

            else -> {
                // No location access granted.
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BirdLocationSDKTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Button(onClick = {
                            clickedButtonId = BUTTON_ID_TIMELY_UPDATE
                            checkForLocationPermission {
                                enableBirdLocationUpdates()
                            }
                        }, modifier = Modifier.padding(8.dp)) {
                            Text(text = "Enable Timely Location updates")
                        }

                        Button(onClick = {
                            clickedButtonId = BUTTON_ID_ONCE
                            checkForLocationPermission {
                                requestLocationOnce()
                            }
                        }, modifier = Modifier.padding(8.dp)) {
                            Text(text = "Request once")
                        }
                    }
                }
            }
        }
    }

    private fun checkForLocationPermission(permissionGranted: () -> Unit) {
        if (ContextCompat.checkSelfPermission(
                this,
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

    private fun enableBirdLocationUpdates() {
        BirdLocationSDK.enableTimelyUpdates(15000)
    }

    private fun requestLocationOnce() {
        BirdLocationSDK.requestLocationUpdateOnce()
    }


}

