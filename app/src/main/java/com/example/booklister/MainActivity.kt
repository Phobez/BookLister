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

        searchInput = findViewById<EditText>(R.id.search_input)

        val onEditorActionListener = TextView.OnEditorActionListener { _, i, _ ->
            var handled = false

            if (i == EditorInfo.IME_ACTION_DONE) {
                searchBooks()
                handled = true
            }

            return@OnEditorActionListener handled
        }

        searchInput.setOnEditorActionListener(onEditorActionListener)

        findViewById<Button>(R.id.search_button).setOnClickListener { searchBooks() }

//        val TEST_BOOK_LIST = ArrayList<Book>()
//
//        TEST_BOOK_LIST.add(Book("The Idiot", listOf<String>("Fyodor Dostoevsky"), "https://google.com"))
//        TEST_BOOK_LIST.add(Book("Kokoro", listOf<String>("Natsume Soseki"), "https://google.com"))
//        TEST_BOOK_LIST.add(Book("The Idiot", listOf<String>("Fyodor Dostoevsky"), "https://google.com"))
//        TEST_BOOK_LIST.add(Book("Kokoro", listOf<String>("Natsume Soseki"), "https://google.com"))
//        TEST_BOOK_LIST.add(Book("The Idiot", listOf<String>("Fyodor Dostoevsky"), "https://google.com"))
//        TEST_BOOK_LIST.add(Book("Kokoro", listOf<String>("Natsume Soseki"), "https://google.com"))
//        TEST_BOOK_LIST.add(Book("The Idiot", listOf<String>("Fyodor Dostoevsky"), "https://google.com"))
//        TEST_BOOK_LIST.add(Book("Kokoro", listOf<String>("Natsume Soseki"), "https://google.com"))
//        TEST_BOOK_LIST.add(Book("The Idiot", listOf<String>("Fyodor Dostoevsky"), "https://google.com"))
//        TEST_BOOK_LIST.add(Book("Kokoro", listOf<String>("Natsume Soseki"), "https://google.com"))


        model.books.observe(this, Observer {
            val list = findViewById<RecyclerView>(R.id.book_list)

            if (list.adapter != null) {
                val adapter = list.adapter as BookListAdapter
                adapter.data = it
                adapter?.notifyDataSetChanged()
            } else {
                val adapter = BookListAdapter(this)
                adapter.data = it
                list.adapter = adapter
            }
        })
    }

    private fun searchBooks() {
        val searchQuery = searchInput.text.toString()
        model.getBooks(searchQuery)
    }
}