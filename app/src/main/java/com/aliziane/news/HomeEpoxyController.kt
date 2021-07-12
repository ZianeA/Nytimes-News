package com.aliziane.news

import com.airbnb.epoxy.AsyncEpoxyController

class HomeEpoxyController : AsyncEpoxyController() {
    var articles = emptyList<Article>()

    override fun buildModels() {
        articles.forEach {
            ArticleEpoxyModel(it)
                .id(it.url)
                .addTo(this)
        }
    }
}
