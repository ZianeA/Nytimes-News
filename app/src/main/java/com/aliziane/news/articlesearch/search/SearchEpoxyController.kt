package com.aliziane.news.articlesearch.search

import com.airbnb.epoxy.AsyncEpoxyController
import com.aliziane.news.articledetails.EpoxyAutoBuild
import com.aliziane.news.home.Article

class SearchEpoxyController : AsyncEpoxyController() {
    var history by EpoxyAutoBuild(emptySet<String>())
    var suggestions by EpoxyAutoBuild(emptyList<Article>())

    var onSuggestionClickListener: ((suggestion: Article) -> Unit)? = null
    var onHistoryClickListener: ((history: String) -> Unit)? = null
    var onHistoryDeleteListener: ((history: String) -> Unit)? = null

    override fun buildModels() {
        history.forEach {
            SearchHistoryEpoxyModel(
                it,
                { _ -> onHistoryClickListener?.invoke(it) },
                { _ -> onHistoryDeleteListener?.invoke(it) }
            )
                .id(it)
                .addTo(this)
        }

        suggestions.forEach { article ->
            SearchSuggestionEpoxyModel(article.title) { onSuggestionClickListener?.invoke(article) }
                .id(article.url)
                .addTo(this)
        }
    }
}