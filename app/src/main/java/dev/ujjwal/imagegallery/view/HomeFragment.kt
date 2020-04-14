package dev.ujjwal.imagegallery.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var viewModel: GalleyViewModel
    private lateinit var flickrResponse: MutableLiveData<FlickrResponse>
    private lateinit var error: MutableLiveData<Boolean>
    private val staggeredRecyclerViewAdapter = StaggeredRecyclerViewAdapter(arrayListOf())
    private val NUM_COLUMNS = 2

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        flickrResponse.observe(viewLifecycleOwner, Observer { flickrResponse ->
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

        error.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it)
                    swipeRefreshLayout.isRefreshing = false
            }
        })
    }
}
