package com.aliziane.news.home

import retrofit2.http.GET
import retrofit2.http.Path


interface StoriesApi {
    @GET("topstories/v2/{section}.json")
    suspend fun getTopStories(@Path("section") section: String = "home"): TopStoriesResponse
}