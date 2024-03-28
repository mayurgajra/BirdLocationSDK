package com.mayurg.locationsdk.domain.use_case

interface OnLocationUpdated {
    fun invoke(lat: Double, lon: Double)
}