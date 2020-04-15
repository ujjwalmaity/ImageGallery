package dev.ujjwal.imagegallery.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrPhotoService {

//    api_key=6f102c62f41998d151e5a1b48713cf13
//    per_page=20
//    page=1

    @GET("services/rest/?method=flickr.photos.getRecent&format=json&nojsoncallback=1&extras=url_s")
    fun getPhotos(
        @Query("page") page: Int,
        @Query("per_page") size: Int,
        @Query("api_key") key: String
    ): Call<FlickrResponse>

//    api_key=6f102c62f41998d151e5a1b48713cf13
//    text=cat

    @GET("services/rest/?method=flickr.photos.search&format=json&nojsoncallback=1&extras=url_s")
    fun getSearchResult(
        @Query("text") query: String,
        @Query("api_key") key: String
    ): Call<FlickrResponse>
}