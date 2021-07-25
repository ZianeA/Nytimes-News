package com.aliziane.news.books

import coil.load
import com.aliziane.news.R
import com.aliziane.news.common.ViewBindingKotlinModel
import com.aliziane.news.databinding.ItemBookBinding

class BookEpoxyModel(private val book: Book) :
    ViewBindingKotlinModel<ItemBookBinding>(R.layout.item_book) {
    override fun ItemBookBinding.bind() {
        cover.load(book.cover) {
            crossfade(true)
            placeholder(R.drawable.ic_placeholder_image)
        }
        title.text = book.title
        contributor.text = book.contributor
    }
}