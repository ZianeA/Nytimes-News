package com.aliziane.news.articlesearch.result

import com.airbnb.epoxy.AsyncEpoxyController
import com.aliziane.news.home.Article
import com.aliziane.news.articledetails.EpoxyAutoBuild

class SearchResultEpoxyController : AsyncEpoxyController() {
    var articles by EpoxyAutoBuild(emptyList<Article>())
    var onSearchResultClickListener: ((article: Article) -> Unit)? = null

    override fun buildModels() {
        articles.forEach { article ->
            SearchResultEpoxyModel(article) { onSearchResultClickListener?.invoke(article) }
                .id(article.url)
                .addTo(this)
        }
    }
}