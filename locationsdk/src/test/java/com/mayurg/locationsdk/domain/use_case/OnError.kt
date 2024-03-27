package com.mayurg.locationsdk.domain.use_case

interface OnError {

    fun invoke(code: Int, message: String)
}