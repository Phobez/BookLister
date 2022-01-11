package com.example.booklister

import android.content.Context
import android.net.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.booklister.databinding.ActivityMainBinding
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    enum class EMPTYVIEW {
        NO_RESULTS, NO_INTERNET, NO_QUERY, DEFAULT
    }

    private lateinit var searchInput: EditText
    private lateinit var emptyView: TextView
    private lateinit var list: RecyclerView
    private lateinit var searchButton: Button
    private val model: BookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        val binding = ActivityMainBinding.inflate(layoutInflater)

        list = binding.bookList
        emptyView = binding.emptyView
        searchInput = binding.searchInput
        searchButton = binding.searchButton

        val onEditorActionListener = TextView.OnEditorActionListener { _, i, _ ->
            var handled = false

            if (i == EditorInfo.IME_ACTION_DONE) {
                Utils.hideKeyboard(this)
                searchBooks()
                handled = true
            }

            return@OnEditorActionListener handled
        }

        searchInput.setOnEditorActionListener(onEditorActionListener)

        searchButton.setOnClickListener {
            Utils.hideKeyboard(this)
            searchBooks()
        }

        val adapter = BookListAdapter(this)

        adapter.data = ArrayList()

        list.adapter = adapter

        list.visibility = View.GONE
        displayEmptyView()

        model.getBooks().observe(this, {
            val adapter = list.adapter as BookListAdapter

            if (it.isEmpty()) {
                list.visibility = View.GONE
                displayEmptyView(EMPTYVIEW.NO_RESULTS)
            } else {
                list.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }

            adapter.data = it
            adapter.notifyDataSetChanged()
        })

        if (!isInternetAvailable()) disableFeatures()

        setContentView(binding.root)
    }

    private fun searchBooks() {
        val searchQuery = searchInput.text.toString()
        if (searchQuery.isBlank()) {
            displayEmptyView(EMPTYVIEW.NO_QUERY)
        } else model.searchBooks(searchQuery)

    }

    private fun displayEmptyView(type: EMPTYVIEW = EMPTYVIEW.DEFAULT) {
        emptyView.text = when (type) {
            EMPTYVIEW.NO_RESULTS -> getString(R.string.no_results)
            EMPTYVIEW.NO_INTERNET -> getString(R.string.no_internet_connection)
            else -> getString(R.string.no_data)
        }
        emptyView.visibility = View.VISIBLE
    }

    private fun isInternetAvailable(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        var isConnected: Boolean = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (cm.activeNetwork != null && cm.getNetworkCapabilities(cm.activeNetwork) != null) {
                isConnected = true
            }
        } else {
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            isConnected = activeNetwork?.isConnectedOrConnecting == true
        }

        return isConnected
    }

    private fun disableFeatures() {
        list.visibility = View.GONE
        displayEmptyView(EMPTYVIEW.NO_INTERNET)
        searchInput.inputType = 0
        searchButton.isEnabled = false
    }
}