package com.aliziane.news.articledetails

import android.view.View
import com.aliziane.news.R
import com.aliziane.news.common.ViewBindingKotlinModel
import com.aliziane.news.databinding.ItemSortCommentsByBinding

data class SortByEpoxyModel(
    private val sort: String,
    private val onClickListener: View.OnClickListener
) :
    ViewBindingKotlinModel<ItemSortCommentsByBinding>(R.layout.item_sort_comments_by) {

    override fun ItemSortCommentsByBinding.bind() {
        sortBy.text = sort
        root.setOnClickListener(onClickListener)
    }
}