package dev.ujjwal.imagegallery.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.ujjwal.imagegallery.model.FlickrPhotoService
import dev.ujjwal.imagegallery.model.FlickrPhotoServiceBuilder
import dev.ujjwal.imagegallery.model.FlickrResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel : ViewModel() {

    private lateinit var flickrResponse: MutableLiveData<FlickrResponse>
    lateinit var queryText: MutableLiveData<String>
    lateinit var isLoading: MutableLiveData<Boolean>
    lateinit var error: MutableLiveData<Boolean>

    private var tempQueryText = ""

    companion object {
        private val TAG = SearchViewModel::class.java.simpleName
    }

    fun getFlickrResponse(): MutableLiveData<FlickrResponse> {
        if (!::flickrResponse.isInitialized) {
            flickrResponse = MutableLiveData()
            queryText = MutableLiveData()
            isLoading = MutableLiveData()
            isLoading.value = false
            error = MutableLiveData()
            error.value = false
        }
        return flickrResponse
    }

    fun makeQuery(query: String) {
        if ((!isLoading.value!!) && (query != "") && (query != tempQueryText)) {
            tempQueryText = query
            fetchFlickrResponse(query)
        }
    }

    private fun fetchFlickrResponse(query: String) {
        Log.i(TAG, "Query text: $query")
        isLoading.value = true
        error.value = false

        val flickrPhotoService = FlickrPhotoServiceBuilder.buildService(FlickrPhotoService::class.java)

        val response = flickrPhotoService.getSearchResult(query, "6f102c62f41998d151e5a1b48713cf13")

        response.enqueue(object : Callback<FlickrResponse> {
            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                tempQueryText = ""
                isLoading.value = false
                error.value = true
            }

            override fun onResponse(call: Call<FlickrResponse>, response: Response<FlickrResponse>) {
                isLoading.value = false
                if (response.isSuccessful) {
                    flickrResponse.value = response.body()
                } else {
                    tempQueryText = ""
                    error.value = true
                }
            }
        })
    }
}