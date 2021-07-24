package com.aliziane.news.books

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BestsellersListsResponse(val results: Results) {
    @Serializable
    data class Results(val lists: List<BestsellersList>)
}

@Serializable
data class BestsellersListResponse(val results: BestsellersList)

@Serializable
data class BestsellersList(
    @SerialName("list_name_encoded") val id: String,
    @SerialName("display_name") val name: String,
    val books: List<Book>
)

@Serializable
data class Book(
    @SerialName("primary_isbn13") val isbn: String,
    val title: String,
    val contributor: String,
    val description: String,
    @SerialName("book_image") val cover: String
)

fun BestsellersListsResponse.toBestsellersLists() = this.results.lists
fun BestsellersListResponse.toBestsellersList() = this.results
