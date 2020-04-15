package dev.ujjwal.imagegallery.view

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import dev.ujjwal.imagegallery.R
import dev.ujjwal.imagegallery.model.FlickrPhoto
import dev.ujjwal.imagegallery.model.FlickrPhotos
import dev.ujjwal.imagegallery.model.FlickrResponse
import dev.ujjwal.imagegallery.viewmodel.SearchViewModel
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import java.util.concurrent.TimeUnit

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView

    private val disposables = CompositeDisposable()
    private var timeSinceLastRequest: Long = 0

    private lateinit var viewModel: SearchViewModel
    private lateinit var flickrResponse: MutableLiveData<FlickrResponse>
    private lateinit var queryText: MutableLiveData<String>
    private lateinit var error: MutableLiveData<Boolean>

    private val searchRecyclerViewAdapter = SearchRecyclerViewAdapter(arrayListOf())
    private val NUM_COLUMNS = 2

    companion object {
        private val TAG = SearchFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        flickrResponse = viewModel.getFlickrResponse()
        queryText = viewModel.queryText
        error = viewModel.error

        observeViewModel()

        search_recycler_view.apply {
            layoutManager = StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayout.VERTICAL)
            adapter = searchRecyclerViewAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        menu.clear()
        inflater.inflate(R.menu.menu_search, menu)

        val item = menu.findItem(R.id.action_search)
        searchView = item.actionView as SearchView

        timeSinceLastRequest = System.currentTimeMillis()

        createDebounceOperator()
    }

    private fun createDebounceOperator() {
        // Create the Observable
        val observableQueryText = Observable
            .create<String> { emitter ->
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        if (!emitter.isDisposed) {
                            emitter.onNext(newText)
                        }
                        return false
                    }
                })
            }
            .debounce(800, TimeUnit.MILLISECONDS)  // Apply Debounce() operator to limit requests
            .subscribeOn(Schedulers.io())

        // Subscribe an Observer
        observableQueryText.subscribe(object : Observer<String> {
            override fun onSubscribe(d: Disposable) {
                disposables.add(d)
            }

            override fun onNext(s: String) {
                val str = "onNext: time since last request: " + (System.currentTimeMillis() - timeSinceLastRequest)
                Log.d(TAG, str)
                Log.d(TAG, "onNext: search query: $s")
                timeSinceLastRequest = System.currentTimeMillis()

                sendRequestToServer(s)
            }

            override fun onError(e: Throwable) {
                Log.d(TAG, "onError: ${e.message}")
            }

            override fun onComplete() {
                Log.d(TAG, "onComplete:")
            }
        })
    }

    private fun sendRequestToServer(query: String) {
        queryText.postValue(query.trim())
    }

    private fun observeViewModel() {
        flickrResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { flickrResponse ->
            flickrResponse?.let {
                val flickrPhotos: FlickrPhotos = it.photos!!
                val listFlickrPhotos: List<FlickrPhoto> = flickrPhotos.photo!!
                searchRecyclerViewAdapter.updatePhoto(listFlickrPhotos)
                for (i in listFlickrPhotos) {
                    Log.i(TAG, "${i.url}")
                }
            }
        })

        viewModel.queryText.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                viewModel.makeQuery(queryText.value!!)
            }
        })

        error.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it) {
                    Snackbar.make(frame_layout, "Network failed", Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY") { viewModel.makeQuery(queryText.value!!) }
                        .show()
                }
            }
        })

        viewModel.isLoading.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                if (it)
                    search_progress_bar.visibility = View.VISIBLE
                else
                    search_progress_bar.visibility = View.GONE
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
