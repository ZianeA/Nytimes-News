package com.aliziane.news.articlesearch.search

import android.view.View
import com.aliziane.news.R
import com.aliziane.news.common.ViewBindingKotlinModel
import com.aliziane.news.databinding.ItemSearchHistoryBinding

class SearchHistoryEpoxyModel(
    private val text: String,
    private val onClickListener: View.OnClickListener,
    private val onDeleteListener: View.OnClickListener
) :
    ViewBindingKotlinModel<ItemSearchHistoryBinding>(R.layout.item_search_history) {
    override fun ItemSearchHistoryBinding.bind() {
        textView.text = text
        root.setOnClickListener(onClickListener)
        deleteButton.setOnClickListener(onDeleteListener)
    }
}