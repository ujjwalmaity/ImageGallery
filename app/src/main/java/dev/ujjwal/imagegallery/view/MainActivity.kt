package dev.ujjwal.imagegallery.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import dev.ujjwal.imagegallery.R
import dev.ujjwal.imagegallery.model.FlickrPhoto
import dev.ujjwal.imagegallery.model.FlickrPhotos
import dev.ujjwal.imagegallery.model.FlickrResponse
import dev.ujjwal.imagegallery.viewmodel.GalleyViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: GalleyViewModel
    private lateinit var flickrResponse: MutableLiveData<FlickrResponse>

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(GalleyViewModel::class.java)
        flickrResponse = viewModel.getFlickrResponse()

        observeViewModel()
    }

    private fun observeViewModel() {
        flickrResponse.observe(this, Observer { flickrResponse ->
            flickrResponse?.let {
                val flickrPhotos: FlickrPhotos = it.photos!!
                val listFlickrPhotos: List<FlickrPhoto> = flickrPhotos.photo!!
                for (i in listFlickrPhotos) {
                    Log.i(TAG, "${i.url}")
                }
            }
        })
    }
}
