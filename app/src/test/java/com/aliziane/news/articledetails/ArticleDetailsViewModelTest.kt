package com.aliziane.news.articledetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.aliziane.news.CoroutineRule
import com.aliziane.news.R
import com.aliziane.news.common.encodeToString
import com.aliziane.news.fakeArticle
import com.jraska.livedata.test
import io.kotest.assertions.assertSoftly
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.datetime.Clock
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class ArticleDetailsViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineRule()

    private val fakeApi = FakeCommunityApi()
    private val savedStateHandle =
        SavedStateHandle(mapOf(ARTICLE_KEY to fakeArticle.encodeToString()))
    private val viewModel =
        ArticleDetailsViewModel(savedStateHandle, fakeApi, coroutineRule.testDispatcherProvider)

    @Test
    fun `display article`() = coroutineRule.runBlockingTest {
        viewModel.article.test()

        viewModel.article.value shouldBe fakeArticle
    }

    @Test
    fun `display comments`() = coroutineRule.runBlockingTest {
        viewModel.comments.test()

        viewModel.comments.value shouldBe fakeApi.response.toComments()
    }

    @Test
    fun `display error message when fetching comments fails`() = coroutineRule.runBlockingTest {
        fakeApi.fail = true

        viewModel.comments.test()

        viewModel.message.test { awaitItem() shouldBe R.string.error_generic }
    }

    @Test
    fun `display progress indicator when loading comments`() = coroutineRule.runBlockingTest {
        val testObserver = viewModel.isLoading.test()

        viewModel.comments.test()

        testObserver.assertValueHistory(false, true, false)
    }

    @Test
    fun `display empty state when there are not comments`() = coroutineRule.runBlockingTest {
        fakeApi.returnEmpty = true

        viewModel.comments.test()

        viewModel.comments.value.shouldBeEmpty()
    }
}

class FakeCommunityApi : CommunityApi {
    var response = createResponse(fakeComment)
    var emptyResponse = createResponse()
    var fail = false
    var returnEmpty = false
    override suspend fun getComments(url: String, sort: CommunityApi.Sort): CommentResponse {
        return when {
            fail -> throw IOException()
            returnEmpty -> emptyResponse
            url == fakeArticle.url -> response
            else -> emptyResponse
        }
    }

    private fun createResponse(vararg comments: Comment) =
        CommentResponse(CommentResponse.Results(comments.toList()))
}

val fakeComment = Comment(
    101,
    "fake author",
    "https://fakeUrl.com/fake_image.png",
    "fake body",
    Clock.System.now(),
    Clock.System.now(),
    202,
    303
)

private const val ARTICLE_KEY = "article"