package com.aliziane.news.articlesearch

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.aliziane.news.CoroutineRule
import com.aliziane.news.R
import com.aliziane.news.fakeDoc
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.jraska.livedata.test
import io.kotest.assertions.assertSoftly
import io.kotest.data.forAll
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
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

        viewModel.onQuerySubmit("new query")

        assertSoftly(viewModel.searchResult.value!!) {
            shouldBe(fakeApi.response.toArticles())
            forAll { it.title shouldContain "new query" }
        }
    }

    @Test
    fun `display search result when submitted query is blank`(
        @TestParameter("", "  ") query: String?
    ) = coroutineRule.runBlockingTest {
        viewModel.searchResult.test()

        viewModel.onQuerySubmit(query)

        viewModel.searchResult.value shouldBe fakeApi.response.toArticles()
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

        viewModel.onQueryChange("new query")
        // Advance time to circumvent debounce
        coroutineRule.testDispatcher.advanceTimeBy(1000)

        assertSoftly(viewModel.searchSuggestions.value!!) {
            shouldBe(fakeApi.response.toArticles().map { it.title })
            forAll { it shouldContain "new query" }
        }
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

        viewModel.onQueryChange("new query")
        // Advance time to circumvent debounce
        coroutineRule.testDispatcher.advanceTimeBy(1000)

        viewModel.message.test { awaitItem() shouldBe R.string.error_generic }
    }

    @Test
    fun `display progress indicator when loading suggestions`() = coroutineRule.runBlockingTest {
        viewModel.searchSuggestions.test()
        val testObserver = viewModel.isLoading.test()

        viewModel.onQueryChange("new query")
        // Advance time to circumvent debounce
        coroutineRule.testDispatcher.advanceTimeBy(1000)

        testObserver.assertValueHistory(false, true, false)
    }
}

class FakeSearchApi : SearchApi {
    private var docs = mutableListOf(fakeDoc)

    var response = SearchResponse(SearchResponse.Response(docs))
    var fail = false

    override suspend fun search(query: String?): SearchResponse {
        if (fail) throw IOException()
        if (query != null) {
            docs.replaceAll { doc ->
                val headline = doc.headline
                doc.copy(headline = headline.copy(main = headline.main + query))
            }
        }
        return response
    }
}
