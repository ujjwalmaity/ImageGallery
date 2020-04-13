package dev.ujjwal.imagegallery.model

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FlickrPhotoServiceBuilder {

    private const val URL = "https://api.flickr.com/"

    // Create logging - In Logcat use Debug filter 'OkHttp'
    private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    // Create OkHttp Client
    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logging)

    // Create Retrofit Builder
    private val builder = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp.build())

    // Create Retrofit Instance
    private val retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }
}