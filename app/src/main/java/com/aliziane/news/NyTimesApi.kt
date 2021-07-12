package com.aliziane.news

import retrofit2.http.GET
import retrofit2.http.Path


interface NyTimesApi {
    @GET("topstories/v2/{section}.json")
    suspend fun getTopStories(@Path("section") section: String = "home"): TopStoriesResponse
}