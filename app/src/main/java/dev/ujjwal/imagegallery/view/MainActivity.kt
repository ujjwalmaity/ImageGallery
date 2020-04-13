package dev.ujjwal.imagegallery.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dev.ujjwal.imagegallery.R
import dev.ujjwal.imagegallery.model.FlickrPhoto
import dev.ujjwal.imagegallery.model.FlickrPhotos
import dev.ujjwal.imagegallery.model.FlickrResponse
import dev.ujjwal.imagegallery.viewmodel.GalleyViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: GalleyViewModel
    private lateinit var flickrResponse: MutableLiveData<FlickrResponse>
    private lateinit var error: MutableLiveData<Boolean>
    private val staggeredRecyclerViewAdapter = StaggeredRecyclerViewAdapter(arrayListOf())
    private val NUM_COLUMNS = 2

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(GalleyViewModel::class.java)
        flickrResponse = viewModel.getFlickrResponse()
        error = viewModel.error

        observeViewModel()

        recycler_view.apply {
            layoutManager = StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayout.VERTICAL)
            adapter = staggeredRecyclerViewAdapter
        }

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun observeViewModel() {
        flickrResponse.observe(this, Observer { flickrResponse ->
            flickrResponse?.let {
                val flickrPhotos: FlickrPhotos = it.photos!!
                val listFlickrPhotos: List<FlickrPhoto> = flickrPhotos.photo!!
                staggeredRecyclerViewAdapter.updatePhoto(listFlickrPhotos)
                swipeRefreshLayout.isRefreshing = false
                for (i in listFlickrPhotos) {
                    Log.i(TAG, "${i.url}")
                }
            }
        })

        error.observe(this, Observer {
            it?.let {
                if (it)
                    swipeRefreshLayout.isRefreshing = false
            }
        })
    }
}
