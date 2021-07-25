package com.aliziane.news.articledetails

import retrofit2.http.GET
import retrofit2.http.Query

interface CommunityApi {
    @GET("community/v3/user-content/url.json")
    suspend fun getComments(
        @Query("url") url: String,
        @Query("sort") sort: Sort
    ): CommentResponse

    enum class Sort(private val value: String) {
        NEWEST("newest"), OLDEST("oldest"), TOP("reader");

        override fun toString() = value
    }
}
