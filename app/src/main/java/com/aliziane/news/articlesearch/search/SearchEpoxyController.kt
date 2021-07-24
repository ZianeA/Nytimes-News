package com.aliziane.news.articlesearch.search

import com.airbnb.epoxy.AsyncEpoxyController
import com.aliziane.news.articledetails.EpoxyAutoBuild

class SearchEpoxyController : AsyncEpoxyController() {
    var history by EpoxyAutoBuild(emptyList<String>())
    var suggestions by EpoxyAutoBuild(emptyList<String>())

    var onItemSelectListener: ((text: String) -> Unit)? = null
    var onItemDeleteListener: ((text: String) -> Unit)? = null

    override fun buildModels() {
        history.forEach {
            SearchHistoryEpoxyModel(
                it,
                { _ -> onItemSelectListener?.invoke(it) },
                { _ -> onItemSelectListener?.invoke(it) }
            )
                .id(it)
                .addTo(this)
        }

        suggestions.forEach {
            SearchSuggestionEpoxyModel(it) { _ -> onItemSelectListener?.invoke(it) }
                .id(it)
                .addTo(this)
        }
    }
}