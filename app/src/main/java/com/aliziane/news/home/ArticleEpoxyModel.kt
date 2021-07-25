package com.aliziane.news.home

import android.text.format.DateUtils
import android.view.View
import coil.load
import com.aliziane.news.R
import com.aliziane.news.common.ViewBindingKotlinModel
import com.aliziane.news.databinding.ItemArticleBinding

data class ArticleEpoxyModel(
    private val article: Article,
    private val onClickListener: View.OnClickListener
) :
    ViewBindingKotlinModel<ItemArticleBinding>(R.layout.item_article) {

    override fun ItemArticleBinding.bind() {
        title.text = article.title
        publishedDate.text =
            DateUtils.getRelativeTimeSpanString(article.publishedDate.toEpochMilliseconds())

        image.load(article.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_placeholder_image)
        }

        root.setOnClickListener(onClickListener)
    }
}