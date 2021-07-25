package com.aliziane.news.books

import com.aliziane.news.R
import com.aliziane.news.common.ViewBindingKotlinModel
import com.aliziane.news.databinding.ItemBestsellersListBinding

data class BestsellersListEpoxyModel(private val listName: String) :
    ViewBindingKotlinModel<ItemBestsellersListBinding>(R.layout.item_bestsellers_list) {
    override fun ItemBestsellersListBinding.bind() {
        root.text = listName
    }
}