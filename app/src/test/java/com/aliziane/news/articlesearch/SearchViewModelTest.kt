package com.aliziane.news.articlesearch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.aliziane.news.CoroutineRule
import com.aliziane.news.R
import com.aliziane.news.common.encodeToString
import com.aliziane.news.fakeArticle
import com.aliziane.news.fakeDoc
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.jraska.livedata.test
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(TestParameterInjector::class)
class SearchViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineRule()

    private val fakeApi = FakeSearchApi()
    private val savedStateHandle = SavedStateHandle()
    private val viewModel =
        SearchViewModel(savedStateHandle, fakeApi, coroutineRule.testDispatcherProvider)

    @Test
    fun `display search result`() = coroutineRule.runBlockingTest {
        viewModel.searchResult.test()

        viewModel.onQuerySubmit(fakeApi.fakeQuery)

        viewModel.searchResult.value shouldBe fakeApi.fakeQueryResponse.toArticles()
    }

    @Test
    fun `display search result when submitted query is blank`(
        @TestParameter("", "  ") query: String?
    ) = coroutineRule.runBlockingTest {
        viewModel.searchResult.test()

        viewModel.onQuerySubmit(query)

        viewModel.searchResult.value shouldBe fakeApi.emptyQueryResponse.toArticles()
    }

    @Test
    fun `display error when searching fails`() = coroutineRule.runBlockingTest {
        fakeApi.fail = true

        viewModel.searchResult.test()

        viewModel.message.test { awaitItem() shouldBe R.string.error_generic }
    }

    @Test
    fun `display progress indicator when searching`() {
        val testObserver = viewModel.isLoading.test()

        viewModel.searchResult.test()

        testObserver.assertValueHistory(false, true, false)
    }

    @Test
    fun `set default submitted query to empty`() = coroutineRule.runBlockingTest {
        viewModel.searchQuery.value shouldBe ""
    }

    @Test
    fun `update submitted query`() = coroutineRule.runBlockingTest {
        viewModel.onQuerySubmit("new query")

        viewModel.searchQuery.value shouldBe "new query"
    }

    @Test
    fun `convert null submitted query to empty`() = coroutineRule.runBlockingTest {
        viewModel.onQuerySubmit(null)

        viewModel.searchQuery.value shouldBe ""
    }

    @Test
    fun `display search suggestions`() = coroutineRule.runBlockingTest {
        viewModel.searchSuggestions.test()

        viewModel.onQueryChange(fakeApi.fakeQuery)
        // Advance time to circumvent debounce
        coroutineRule.testDispatcher.advanceTimeBy(1000)

        viewModel.searchSuggestions.value shouldBe fakeApi.fakeQueryResponse.toArticles()
    }

    @Test
    fun `display no suggestions when query is blank or null`(
        @TestParameter("", "  ", "null") query: String?
    ) = coroutineRule.runBlockingTest {
        viewModel.searchSuggestions.test()

        viewModel.onQueryChange(query)
        // Advance time to circumvent debounce
        coroutineRule.testDispatcher.advanceTimeBy(1000)

        viewModel.searchSuggestions.value.shouldBeEmpty()
    }

    @Test
    fun `display error when fetching suggestions fails`() = coroutineRule.runBlockingTest {
        fakeApi.fail = true
        viewModel.searchSuggestions.test()

        viewModel.onQueryChange(fakeApi.fakeQuery)
        // Advance time to circumvent debounce
        coroutineRule.testDispatcher.advanceTimeBy(1000)

        viewModel.message.test { awaitItem() shouldBe R.string.error_generic }
    }

    @Test
    fun `display progress indicator when loading suggestions`() = coroutineRule.runBlockingTest {
        viewModel.searchSuggestions.test()
        val testObserver = viewModel.isLoading.test()

        viewModel.onQueryChange(fakeApi.fakeQuery)
        // Advance time to circumvent debounce
        coroutineRule.testDispatcher.advanceTimeBy(1000)

        testObserver.assertValueHistory(false, true, false)
    }

    @Test
    fun `navigate to article details when article is clicked`() = coroutineRule.runBlockingTest {
        viewModel.onArticleClick(fakeArticle)

        viewModel.navigateToArticleDetails.test { awaitItem() shouldBe fakeArticle.encodeToString() }
    }
}

class FakeSearchApi : SearchApi {
    var fakeQuery = "fake query"
    var fakeQueryResponse =
        createResponse(fakeDoc.copy(headline = fakeDoc.headline.copy(main = fakeQuery)))
    var emptyQueryResponse = createResponse(fakeDoc)
    var emptyResponse = createResponse()
    var fail = false

    override suspend fun search(query: String?): SearchResponse {
        return when {
            fail -> throw IOException()
            query.isNullOrBlank() -> emptyQueryResponse
            query == fakeQuery -> fakeQueryResponse
            else -> emptyResponse
        }
    }

    private fun createResponse(vararg docs: SearchResponse.Response.Doc) =
        SearchResponse(SearchResponse.Response(docs.toList()))
}
