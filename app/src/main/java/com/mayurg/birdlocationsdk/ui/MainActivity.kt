package com.mayurg.birdlocationsdk.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import com.mayurg.birdlocationsdk.R
import com.mayurg.birdlocationsdk.ui.theme.BirdLocationSDKTheme
import com.mayurg.birdlocationsdk.utils.PermissionUtil
import com.mayurg.locationsdk.BirdLocationSDK

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

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
                            Text(text = getString(R.string.enable_timely_location_updates))
                        }

                        Button(onClick = {
                            clickedButtonId = BUTTON_ID_ONCE
                            checkForLocationPermission {
                                requestLocationOnce()
                            }
                        }, modifier = Modifier.padding(8.dp)) {
                            Text(text = getString(R.string.request_once))
                        }

                        val resultState = viewModel.state.value

                        if (resultState.isSuccessful) {
                            Text(
                                text = "Lat: ${resultState.latitude} \n Long: ${resultState.longitude}",
                                modifier = Modifier.padding(8.dp)
                            )
                        } else if (resultState.error.isNotEmpty() && resultState.errorCode != 0) {
                            Text(
                                text = "Code: ${resultState.errorCode} \n Message: ${resultState.error}",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun checkForLocationPermission(permissionGranted: () -> Unit) {
        PermissionUtil.checkForLocationPermission(this, permissionGranted, locationPermissionRequest)
    }

    private fun enableBirdLocationUpdates() {
        BirdLocationSDK.enableTimelyUpdates(15000, { lat, long ->
            viewModel.onLocationUpdate(lat, long)
        }, { errorCode, errorMessage ->
            viewModel.onLocationError(errorMessage, errorCode)
        })
    }

    private fun requestLocationOnce() {
        BirdLocationSDK.requestLocationUpdateOnce({ lat, long ->
            viewModel.onLocationUpdate(lat, long)
        }, { errorCode, errorMessage ->
            viewModel.onLocationError(errorMessage, errorCode)
        })
    }


}

