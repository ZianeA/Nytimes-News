package com.aliziane.news.articledetails

import android.view.View
import coil.load
import com.aliziane.news.home.Article
import com.aliziane.news.R
import com.aliziane.news.common.ViewBindingKotlinModel
import com.aliziane.news.databinding.ItemArticleDetailsBinding
import com.aliziane.news.format

data class ArticleDetailsEpoxyModel(
    private val article: Article,
    private val clickListener: View.OnClickListener
) :
    ViewBindingKotlinModel<ItemArticleDetailsBinding>(R.layout.item_article_details) {

    override fun ItemArticleDetailsBinding.bind() {
        title.text = article.title
        articleAbstract.text = article.abstract
        image.load(article.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_placeholder_image)
        }
        byline.text = article.byline
        publishedAndUpdatedDate.text = root.resources.getString(
            R.string.published_and_update_date,
            article.publishedDate.format(),
            article.updatedDate.format()
        )
        buttonReadMore.setOnClickListener(clickListener)
    }
}