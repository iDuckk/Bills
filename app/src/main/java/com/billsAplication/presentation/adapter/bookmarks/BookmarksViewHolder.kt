package com.billsAplication.presentation.adapter.bookmarks

import androidx.recyclerview.widget.RecyclerView
import com.billsAplication.databinding.BookmarksItemBinding

class BookmarksViewHolder(binding: BookmarksItemBinding) : RecyclerView.ViewHolder(binding.root) {

    val tv_category = binding.tvBookmarkCategory
    val tv_item_amount = binding.tvBookmarkAmount
    val tv_item_note = binding.tvBookmarkNote

    val cardVIew = binding.cardViewBookmarksItem

}
