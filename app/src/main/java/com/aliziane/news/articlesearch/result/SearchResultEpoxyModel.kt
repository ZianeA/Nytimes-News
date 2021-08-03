package com.aliziane.news.articlesearch.result

import android.view.View
import coil.load
import com.aliziane.news.*
import com.aliziane.news.common.ViewBindingKotlinModel
import com.aliziane.news.common.hide
import com.aliziane.news.databinding.ItemSearchResultBinding
import com.aliziane.news.home.Article

class SearchResultEpoxyModel(
    private val article: Article,
    private val onClickListener: View.OnClickListener
) :
    ViewBindingKotlinModel<ItemSearchResultBinding>(R.layout.item_search_result) {
    override fun ItemSearchResultBinding.bind() {
        if (article.kicker == null) kicker.hide() else kicker.text = article.kicker

        title.text = article.title

        if (article.imageUrl == null) {
            image.hide()
        } else {
            image.load(article.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_placeholder_image)
            }
        }

        dateAndByline.text = root.resources.getString(
            R.string.published_date_and_byline,
            article.publishedDate.format(),
            article.byline
        )

        root.setOnClickListener(onClickListener)
    }
}