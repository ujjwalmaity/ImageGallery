package dev.ujjwal.imagegallery.model

import com.google.gson.annotations.SerializedName

class FlickrPhoto {
    var title: String? = null

    @field:SerializedName("url_s")
    var url: String? = null

    @field:SerializedName("width_s")
    var width: String? = null

    @field:SerializedName("height_s")
    var height: String? = null
}

class FlickrPhotos {
    var page: Int = 0
    var pages: Int = 0
    var perpage: Int = 0
    var total: Int = 0

    var photo: List<FlickrPhoto>? = null
}

class FlickrResponse {
    var stat: String? = null

    var photos: FlickrPhotos? = null
}
