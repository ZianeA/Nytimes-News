package com.aliziane.news.articledetails

import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.SimpleEpoxyModel
import com.aliziane.news.Article

class ArticleDetailsEpoxyController : AsyncEpoxyController() {
    var article by EpoxyAutoBuild<Article?>(null)
    var comments by EpoxyAutoBuild(emptyList<Comment>())
    var sortBy by EpoxyAutoBuild("New")

    override fun buildModels() {
        article?.let {
            ArticleDetailsEpoxyModel(it)
                .id(it.url)
                .addTo(this)
        }

        SortByEpoxyModel(sortBy)
            .id(sortBy)
            .addTo(this)

        comments.forEach {
            CommentEpoxyModel(it)
                .id(it.id)
                .addTo(this)
        }
    }
}