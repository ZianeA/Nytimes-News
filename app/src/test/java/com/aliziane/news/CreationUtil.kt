package com.aliziane.news

import com.aliziane.news.articlesearch.SearchResponse
import com.aliziane.news.home.Article
import kotlinx.datetime.Clock

val fakeMultimedia = Article.Multimedia(
    "https://fakeUrl.com/fake_image.png",
    100,
    100,
    "fake caption"
)
val fakeArticle = Article(
    "fake title",
    "fake abstract",
    "fake kicker",
    "https://fakeUrl.com",
    "fake byline",
    Clock.System.now(),
    Clock.System.now(),
    listOf(fakeMultimedia)
)
val fakeDocHeadline = SearchResponse.Response.Doc.Headline(fakeArticle.title, "fake kicker")
val fakeDocByLine = SearchResponse.Response.Doc.ByLine(fakeArticle.byline)
val fakeDocMultimedia =
    fakeMultimedia.run { SearchResponse.Response.Doc.Multimedia(url, height, width, caption) }
val fakeDoc =
    SearchResponse.Response.Doc(
        fakeDocHeadline,
        fakeArticle.abstract,
        fakeArticle.url,
        fakeDocByLine,
        fakeArticle.publishedDate,
        listOf(fakeDocMultimedia)
    )
