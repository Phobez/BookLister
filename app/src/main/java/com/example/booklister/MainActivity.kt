package com.example.booklister

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var searchInput: EditText
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

        model.getBooks().observe(this, {
            if (list.adapter != null) {
                val adapter = list.adapter as BookListAdapter
                adapter.data = it
                adapter.notifyDataSetChanged()
            } else {
                val adapter = BookListAdapter(this)
                adapter.data = it
                list.adapter = adapter
            }
        })
    }

    private fun searchBooks() {
        val searchQuery = searchInput.text.toString()
        model.searchBooks(searchQuery)
    }
}