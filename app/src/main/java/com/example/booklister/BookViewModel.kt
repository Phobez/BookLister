package com.example.booklister

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class BookViewModel : ViewModel() {
    private val books: MutableLiveData<List<Book>> by lazy {
        MutableLiveData<List<Book>>()
    }

    fun getBooks(): LiveData<List<Book>> {
        return books
    }

    fun searchBooks(searchQuery: String) {
        val newBooks = ArrayList<Book>()

        // val url = URL("https://www.googleapis.com/books/v1/volumes?q=${searchQuery}&maxResults=20")
        val url = URL("https://www.googleapis.com/books/v5/volumes?q=${searchQuery}&maxResults=20")

        viewModelScope.launch {
            val res = makeHttpRequest(url)

            if (res.isNotBlank()) {
                val resObj = JSONObject(res)

                if (resObj.has("items")) {
                    val items = resObj.getJSONArray("items")

                    val maxIndex = items.length() - 1

                    for (i in 0..maxIndex) {
                        val item = items.getJSONObject(i).getJSONObject("volumeInfo")
                        val title = item.getString("title")

                        val authors = ArrayList<String>()

                        if (item.has("authors")) {
                            val authorsRaw = item.getJSONArray("authors")

                            val maxAuthorsRawIndex = authorsRaw.length() - 1

                            for (j in 0..maxAuthorsRawIndex) {
                                authors.add(authorsRaw.getString(j))
                            }
                        }

                        val link = item.getString("canonicalVolumeLink")

                        newBooks.add(Book(title, authors, link))
                    }
                }
            }

            books.postValue(newBooks)
        }
    }

    private suspend fun makeHttpRequest(url: URL): String {
        return withContext(Dispatchers.IO) {
            val urlConnection = url.openConnection() as HttpURLConnection
            var inputStream: InputStream? = null
            var res = ""

            try {
                urlConnection.readTimeout = 10000
                urlConnection.connectTimeout = 15000
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                if (urlConnection.responseCode == 200) {
                    inputStream = urlConnection.inputStream
                    val output = StringBuilder()

                    val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
                    val reader = BufferedReader(inputStreamReader)
                    var line = reader.readLine()

                    while (line != null) {
                        output.append(line)
                        line = reader.readLine()
                    }

                    res = output.toString()
                } else {
                    Timber.e("Error " + urlConnection.responseCode)
                }
            } catch (e: IOException) {
                Timber.e("Problem retrieving book JSON results:", e)
            } finally {
                urlConnection.disconnect()
                inputStream?.close()
            }

            res
        }
    }
}