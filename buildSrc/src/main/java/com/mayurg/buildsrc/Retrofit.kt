package com.mayurg.buildsrc

object Retrofit {
    private const val version = "2.10.0"
    const val retrofit = "com.squareup.retrofit2:retrofit:$version"
    const val moshiConverter = "com.squareup.retrofit2:converter-moshi:$version"

    const val moshiAdapter = "com.squareup.moshi:moshi-kotlin:1.12.0"

    private const val okHttpVersion = "5.0.0-alpha.12"
    const val okHttp = "com.squareup.okhttp3:okhttp:$okHttpVersion"
    const val okHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$okHttpVersion"
}