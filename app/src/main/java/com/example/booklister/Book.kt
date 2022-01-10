package com.example.booklister

class Book(val title: String, private val authors: List<String>, val link: String) {
    fun getAuthors(): String {
        return when (authors.count()) {
            1 -> authors[0]
            2 -> authors.subList(0, 1).joinToString(" & ")
            else -> authors.joinToString(", ")
        }
    }
}