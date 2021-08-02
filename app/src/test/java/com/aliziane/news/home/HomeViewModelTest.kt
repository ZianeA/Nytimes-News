package com.aliziane.news.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.aliziane.news.CoroutineRule
import com.aliziane.news.R
import com.aliziane.news.common.encodeToString
import com.jraska.livedata.test
import io.kotest.matchers.shouldBe
import kotlinx.datetime.Clock
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class HomeViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineRule()

    private val fakeApi = FakeStoriesApi()
    private val viewModel =
        HomeViewModel(SavedStateHandle(), fakeApi, coroutineRule.testDispatcherProvider)

    @Test
    fun `display articles`() = coroutineRule.runBlockingTest {
        viewModel.articles.test()

        viewModel.articles.value shouldBe fakeApi.response.stories
    }

    @Test
    fun `display error message when fetching articles fails`() = coroutineRule.runBlockingTest {
        fakeApi.fail = true

        viewModel.articles.test()

        viewModel.message.test { awaitItem() shouldBe R.string.error_generic }
    }

    @Test
    fun `display loading indicator when fetching articles`() {
        val testObserver = viewModel.isLoading.test()

        viewModel.articles.test()

        testObserver.assertValueHistory(false, true, false)
    }

    @Test
    fun `navigate to article details when article is clicked`() = coroutineRule.runBlockingTest {
        viewModel.articles.test()

        viewModel.onArticleClick(fakeArticle)

        viewModel.navigateToArticleDetails.test { awaitItem() shouldBe fakeArticle.encodeToString() }
    }
}

class FakeStoriesApi : StoriesApi {
    var response = TopStoriesResponse(listOf(fakeArticle))
    var fail = false

    override suspend fun getTopStories(section: String): TopStoriesResponse {
        if (fail) throw IOException()
        return response
    }
}

val fakeMultimedia =
    Article.Multimedia("https://fakeUrl.com/fake_image.png", 100, 100, "fake caption")
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