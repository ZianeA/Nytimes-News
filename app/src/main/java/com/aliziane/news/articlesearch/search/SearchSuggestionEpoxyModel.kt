package com.aliziane.news.articlesearch.search

import android.view.View
import com.aliziane.news.R
import com.aliziane.news.common.ViewBindingKotlinModel
import com.aliziane.news.databinding.ItemSearchSuggestionBinding

class SearchSuggestionEpoxyModel(
    private val text: String,
    private val onClickListener: View.OnClickListener
) :
    ViewBindingKotlinModel<ItemSearchSuggestionBinding>(R.layout.item_search_suggestion) {
    override fun ItemSearchSuggestionBinding.bind() {
        root.text = text
        root.setOnClickListener(onClickListener)
    }
}