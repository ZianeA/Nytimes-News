package com.aliziane.news.articledetails

import com.aliziane.news.R
import com.aliziane.news.ViewBindingKotlinModel
import com.aliziane.news.databinding.ItemSortCommentsByBinding

data class SortByEpoxyModel(private val sort: String) :
    ViewBindingKotlinModel<ItemSortCommentsByBinding>(R.layout.item_sort_comments_by) {

    override fun ItemSortCommentsByBinding.bind() {
        sortBy.text = sort
    }
}