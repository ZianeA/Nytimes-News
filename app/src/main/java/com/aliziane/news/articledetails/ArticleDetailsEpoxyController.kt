package com.aliziane.news.articledetails

import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.SimpleEpoxyModel
import com.aliziane.news.R
import com.aliziane.news.home.Article

class ArticleDetailsEpoxyController : AsyncEpoxyController() {
    var article by EpoxyAutoBuild<Article?>(null)
    var comments by EpoxyAutoBuild<List<Comment>?>(null)
    var sortBy by EpoxyAutoBuild("")
    var isLoading by EpoxyAutoBuild(false)

    var onSortByClickListener: (() -> Unit)? = null

    override fun buildModels() {
        article?.let {
            ArticleDetailsEpoxyModel(it)
                .id(it.url)
                .addTo(this)
        }

        SortByEpoxyModel(sortBy) { onSortByClickListener?.invoke() }
            .id(sortBy)
            .addTo(this)

        if (isLoading) {
            SimpleEpoxyModel(R.layout.item_comment_loading_state)
                .id("Comment Loading State")
                .addTo(this)
        } else {
            comments?.forEach {
                CommentEpoxyModel(it)
                    .id(it.id)
                    .addTo(this)
            }

            SimpleEpoxyModel(R.layout.item_comment_empty_state)
                .id("Comment Empty State")
                .addIf(comments?.isEmpty() ?: false, this)
        }
    }
}