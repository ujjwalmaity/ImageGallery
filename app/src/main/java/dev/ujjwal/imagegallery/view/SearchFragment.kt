package dev.ujjwal.imagegallery.view

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import dev.ujjwal.imagegallery.R
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SearchFragment : Fragment() {

    private lateinit var searchView: SearchView

    private val disposables = CompositeDisposable()
    private var timeSinceLastRequest: Long = 0

    companion object {
        private val TAG = SearchFragment::class.java.simpleName
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_search, container, false)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }
}
