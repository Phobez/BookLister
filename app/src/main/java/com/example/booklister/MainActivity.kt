package com.example.booklister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    enum class EMPTY_VIEW {
        NO_RESULTS, NO_INTERNET, NO_QUERY, DEFAULT
    }

    private lateinit var searchInput: EditText
    private lateinit var emptyView: TextView
    private val model: BookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.plant(Timber.DebugTree())

        searchInput = findViewById(R.id.search_input)

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

        findViewById<Button>(R.id.search_button).setOnClickListener {
            Utils.hideKeyboard(this)
            searchBooks()
        }

        val list = findViewById<RecyclerView>(R.id.book_list)

        val adapter = BookListAdapter(this)

        adapter.data = ArrayList()

        list.adapter = adapter

        emptyView = findViewById(R.id.empty_view)

        list.visibility = View.GONE
        displayEmptyView()

        model.getBooks().observe(this, {
            val adapter = list.adapter as BookListAdapter

            if (it.isEmpty()) {
                list.visibility = View.GONE
                displayEmptyView(EMPTY_VIEW.NO_RESULTS)
            } else {
                list.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
            }

            adapter.data = it
            adapter.notifyDataSetChanged()
        })
    }

    private fun searchBooks() {
        val searchQuery = searchInput.text.toString()
        if (searchQuery.isBlank()) {
            displayEmptyView(EMPTY_VIEW.NO_QUERY)
        } else model.searchBooks(searchQuery)

    }

    private fun displayEmptyView(type: EMPTY_VIEW = EMPTY_VIEW.DEFAULT) {
        emptyView.text = when (type) {
            EMPTY_VIEW.NO_RESULTS -> getString(R.string.no_results)
            EMPTY_VIEW.NO_INTERNET -> getString(R.string.no_internet_connection)
            else -> getString(R.string.no_data)
        }
        emptyView.visibility = View.VISIBLE
    }
}