package com.aliziane.news.books

import retrofit2.http.GET
import retrofit2.http.Path

interface BooksApi {
    /**
     * Get top 5 books for all the Best Sellers lists for specified date.
     */
    @GET("books/v3/lists/overview.json")
    suspend fun getBestsellersLists(): BestsellersListsResponse

    /**
     * Get top books for specified Best Sellers lists.
     */
    @GET("books/v3/lists/current/{list}.json")
    suspend fun getBestsellersListById(@Path("list") listId: String): BestsellersListResponse
}