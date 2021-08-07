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
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
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
        viewModel.article.test().value() shouldBe fakeArticle
    }

    @Test
    fun `display comments`() = coroutineRule.runBlockingTest {
        viewModel.comments.test().value() shouldBe fakeApi.newResponse.toComments()
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

        viewModel.comments.test().value().shouldBeEmpty()
    }

    @Test
    fun `sort comments`() {
        val testObserver = viewModel.comments.test()

        assertSoftly {
            viewModel.onSortByChange(SortBy.NEW)
            testObserver.value() shouldBe fakeApi.newResponse.toComments()

            viewModel.onSortByChange(SortBy.TOP)
            testObserver.value() shouldBe fakeApi.topResponse.toComments()

            viewModel.onSortByChange(SortBy.OLD)
            testObserver.value() shouldBe fakeApi.oldResponse.toComments()
        }
    }
}

class FakeCommunityApi : CommunityApi {
    var newResponse = createResponse(fakeComment, fakeComment2, fakeComment3)
    var oldResponse = createResponse(fakeComment3, fakeComment2, fakeComment)
    var topResponse = createResponse(fakeComment2, fakeComment3, fakeComment2)
    var emptyResponse = createResponse()

    var fail = false
    var returnEmpty = false

    override suspend fun getComments(url: String, sort: CommunityApi.Sort): CommentResponse {
        return when {
            fail -> throw IOException()
            returnEmpty -> emptyResponse
            url == fakeArticle.url -> when (sort) {
                CommunityApi.Sort.NEWEST -> newResponse
                CommunityApi.Sort.OLDEST -> oldResponse
                CommunityApi.Sort.READER -> topResponse
            }
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
val fakeComment2 = fakeComment.copy(id = 202)
val fakeComment3 = fakeComment.copy(id = 303)

private const val ARTICLE_KEY = "article"