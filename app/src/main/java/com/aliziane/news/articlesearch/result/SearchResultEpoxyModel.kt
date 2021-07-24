package com.aliziane.news.articlesearch.result

import coil.load
import com.aliziane.news.*
import com.aliziane.news.databinding.ItemSearchResultBinding

class SearchResultEpoxyModel(private val article: Article) :
    ViewBindingKotlinModel<ItemSearchResultBinding>(R.layout.item_search_result) {
    override fun ItemSearchResultBinding.bind() {
        if (article.kicker == null) kicker.hide() else kicker.text = article.kicker
        title.text = article.title
        if (article.imageUrl == null) {
            image.hide()
        } else {
            image.load(article.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_image)
            }
        }
        dateAndByline.text = root.resources.getString(
            R.string.published_date_and_byline,
            article.publishedDate.format(),
            article.byline
        )
    }
}