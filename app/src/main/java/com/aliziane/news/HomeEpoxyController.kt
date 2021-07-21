package com.aliziane.news

import com.airbnb.epoxy.AsyncEpoxyController

class HomeEpoxyController : AsyncEpoxyController() {
    var articles = emptyList<Article>()

    var onArticleClickListener: ((Article) -> Unit)? = null

    override fun buildModels() {
        articles.forEach {
            ArticleEpoxyModel(it) { _ -> onArticleClickListener?.invoke(it) }
                .id(it.url)
                .addTo(this)
        }
    }
}
