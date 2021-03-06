package com.example.booklister

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.booklister.databinding.BookItemViewBinding

class BookListAdapter(private val context: Context) :
    RecyclerView.Adapter<BookListAdapter.ViewHolder>() {
    class ViewHolder(binding: BookItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.bookTitle
        val authors: TextView = binding.bookAuthors
    }

    var data = listOf<Book>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            BookItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.title.text = item.title
        holder.authors.text = item.getAuthors()
        holder.itemView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = data.size
}