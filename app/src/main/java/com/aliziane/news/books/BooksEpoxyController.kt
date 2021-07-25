package com.aliziane.news.books

import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.carousel
import com.aliziane.news.articledetails.EpoxyAutoBuild
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper

class BooksEpoxyController : AsyncEpoxyController() {
    var lists by EpoxyAutoBuild(emptyList<BestsellersList>())

    init {
        Carousel.setDefaultGlobalSnapHelperFactory(null)
    }

    override fun buildModels() {
        lists.forEach { list ->
            BestsellersListEpoxyModel(list.name)
                .id(list.id)
                .addTo(this)

            carousel {
                id("carousel ${list.id}")

                val bookModels = list.books.map { book -> BookEpoxyModel(book).id(book.isbn) }
                models(bookModels)

                val snapHelper = GravitySnapHelper(Gravity.START)
                onBind { _, carousel, _ ->
                    snapHelper.attachToRecyclerView(carousel)
                    carousel.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                }
            }
        }
    }
}