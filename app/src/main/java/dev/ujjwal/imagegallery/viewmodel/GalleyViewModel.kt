package dev.ujjwal.imagegallery.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.ujjwal.imagegallery.model.FlickrPhotoService
import dev.ujjwal.imagegallery.model.FlickrPhotoServiceBuilder
import dev.ujjwal.imagegallery.model.FlickrResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GalleyViewModel : ViewModel() {

    private lateinit var flickrResponse: MutableLiveData<FlickrResponse>
    lateinit var error: MutableLiveData<Boolean>

    fun getFlickrResponse(): MutableLiveData<FlickrResponse> {
        if (!::flickrResponse.isInitialized) {
            flickrResponse = MutableLiveData()
            error = MutableLiveData()
            fetchFlickrResponse()
        }

        return flickrResponse
    }

    fun refresh() {
        fetchFlickrResponse()
    }

    private fun fetchFlickrResponse() {
        error.value = false

        val flickrPhotoService = FlickrPhotoServiceBuilder.buildService(FlickrPhotoService::class.java)

        val response = flickrPhotoService.getPhotos(1, 20, "6f102c62f41998d151e5a1b48713cf13")

        response.enqueue(object : Callback<FlickrResponse> {
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                error.value = true
            }

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                if (response.isSuccessful) {
                    flickrResponse.value = response.body()
                }else{
                    error.value = true
                }
            }
        })
    }
}