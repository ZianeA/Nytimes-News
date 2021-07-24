package com.aliziane.news.articlesearch

import com.aliziane.news.Article
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResponse(val response: Response) {
    @Serializable
    data class Response(val docs: List<Doc>) {
        @Serializable
        data class Doc(
            val headline: Headline,
            val abstract: String,
            @SerialName("web_url") val webUrl: String,
            val byline: ByLine,
            @SerialName("pub_date") @Serializable(with = InstantAsStringSerializer::class)
            val publishedDate: Instant,
            val multimedia: List<Multimedia>
        ) {
            @Serializable
            data class ByLine(val original: String?)

            @Serializable
            data class Headline(val main: String, val kicker: String?)

            @Serializable
            data class Multimedia(
                val url: String, val height: Int, val width: Int, val caption: String?
            )
        }
    }
}

fun SearchResponse.toArticles() = response.docs.mapNotNull {
    // Filter out articles with no byline
    if (it.byline.original == null) null else it.toArticle()
}

private fun SearchResponse.Response.Doc.toArticle(): Article {
    return Article(
        headline.main,
        abstract,
        headline.kicker,
        webUrl,
        requireNotNull(byline.original),
        publishedDate,
        publishedDate,
        multimedia.map { it.toArticleMultimedia() }
    )
}

private fun SearchResponse.Response.Doc.Multimedia.toArticleMultimedia() =
    Article.Multimedia(IMAGE_BASE_URL + url, height, width, caption)

private const val IMAGE_BASE_URL = "https://static01.nyt.com/"

