package com.aliziane.news.articledetails

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponse(val results: Results) {
    @Serializable
    data class Results(val comments: List<Comment>)
}

@Serializable
data class Comment(
    @SerialName("commentID") val id: Int,
    @SerialName("userDisplayName") val author: String,
    @SerialName("picURL") val avatarUrl: String?,
    @SerialName("commentBody") val body: String,
    @SerialName("createDate") @Serializable(with = InstantAsLongSerializer::class)
    val publishedDate: Instant,
    @SerialName("updateDate") @Serializable(with = InstantAsLongSerializer::class)
    val updatedDate: Instant,
    val recommendations: Int,
    val replyCount: Int
)