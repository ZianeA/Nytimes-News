package com.aliziane.news.articlesearch.result

import com.airbnb.epoxy.AsyncEpoxyController
import com.aliziane.news.home.Article
import com.aliziane.news.articledetails.EpoxyAutoBuild

class SearchResultEpoxyController : AsyncEpoxyController() {
    var articles by EpoxyAutoBuild(emptyList<Article>())

    override fun buildModels() {
        articles.forEach {
            SearchResultEpoxyModel(it)
                .id(it.url)
                .addTo(this)
        }
    }
}