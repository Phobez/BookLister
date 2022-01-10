package com.example.booklister

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

class BookViewModel : ViewModel() {
    suspend fun searchBooks(searchQuery: String) {
        val newBooks = ArrayList<Book>()

        val url = URL("https://www.googleapis.com/books/v1/volumes?q=${searchQuery}&maxResults=10")

        val res = makeHttpRequest(url)

        val resObj = JSONObject(res)

    }

    private suspend fun makeHttpRequest(url: URL): String {
        var res = ""

        return withContext(Dispatchers.IO) {
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.readTimeout = 10000
            urlConnection.connectTimeout = 15000
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            val inputStream = urlConnection.inputStream
            val output = StringBuilder()

            val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()

            while (line != null) {
                output.append(line)
                line = reader.readLine()
            }

            res = output.toString()

            res
        }
    }
}