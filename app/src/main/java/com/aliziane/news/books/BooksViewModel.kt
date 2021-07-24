package com.aliziane.news.books

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import javax.inject.Inject

class BooksViewModel @Inject constructor(private val booksApi: BooksApi) : ViewModel() {
    val bestsellersLists = liveData { emit(booksApi.getBestsellersLists().toBestsellersLists()) }
}