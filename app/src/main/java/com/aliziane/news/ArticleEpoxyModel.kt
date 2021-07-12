package com.aliziane.news

import android.text.format.DateUtils
import coil.load
import coil.transform.CircleCropTransformation
import com.aliziane.news.databinding.ItemArticleBinding

data class ArticleEpoxyModel(val article: Article) :
    ViewBindingKotlinModel<ItemArticleBinding>(R.layout.item_article) {

    override fun ItemArticleBinding.bind() {
        title.text = article.title
        publishedDate.text =
            DateUtils.getRelativeTimeSpanString(article.publishedDate.toEpochMilliseconds())

        image.load(article.multimedia.first().url) {
            crossfade(true)
            placeholder(R.drawable.ic_image)
            /*transformations(CircleCropTransformation())*/
        }
    }
}