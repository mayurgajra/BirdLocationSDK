package com.mayurg.locationsdk.utils

internal sealed class Result<out T> {
    data class Success<out T>(val value: T) : Result<T>()
    data class Failure(val errorCode: Int, val message: String) : Result<Nothing>()
}