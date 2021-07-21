package com.aliziane.news.articledetails

import coil.load
import com.aliziane.news.Article
import com.aliziane.news.R
import com.aliziane.news.ViewBindingKotlinModel
import com.aliziane.news.databinding.ItemArticleDetailsBinding
import kotlinx.datetime.toJavaInstant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

data class ArticleDetailsEpoxyModel(private val article: Article) :
    ViewBindingKotlinModel<ItemArticleDetailsBinding>(R.layout.item_article_details) {

    override fun ItemArticleDetailsBinding.bind() {
        title.text = article.title
        articleAbstract.text = article.abstract
        image.load(article.multimedia.first().url)
        byline.text = article.byline

        val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
            .withLocale(Locale.US)
            .withZone(ZoneId.systemDefault())
        publishedDate.text = formatter.format(article.publishedDate.toJavaInstant())
        updatedDate.text = formatter.format(article.updatedDate.toJavaInstant())
    }
}