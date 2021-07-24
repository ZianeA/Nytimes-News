package com.aliziane.news.articlesearch

import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("search/v2/articlesearch.json")
    suspend fun search(@Query("q") query: String? = null): SearchResponse
}
