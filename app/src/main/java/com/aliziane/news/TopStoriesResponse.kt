package com.aliziane.news

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopStoriesResponse(@SerialName("results") val stories: List<Article>)

@Serializable
data class Article(
    val title: String,
    val abstract: String,
    val kicker: String?,
    val url: String,
    val byline: String,
    @SerialName("updated_date") val updatedDate: Instant,
    @SerialName("published_date") val publishedDate: Instant,
    val multimedia: List<Multimedia>
) {
    val imageUrl = multimedia.firstOrNull()?.url

    @Serializable
    data class Multimedia(val url: String, val height: Int, val width: Int, val caption: String?)
}
