package com.aliziane.news.books

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.aliziane.news.CoroutineRule
import com.aliziane.news.R
import com.jraska.livedata.test
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import java.io.IOException

class BooksViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineRule = CoroutineRule()

    private val fakeApi = FakeBooksApi()
    private val viewModel = BooksViewModel(fakeApi, coroutineRule.testDispatcherProvider)

    @Test
    fun `display books`() = coroutineRule.runBlockingTest {
        viewModel.bestsellersLists.test()

        viewModel.bestsellersLists.value shouldBe fakeApi.response.toBestsellersLists()
    }

    @Test
    fun `display error when fetching books fails`() = coroutineRule.runBlockingTest {
        fakeApi.fail = true

        viewModel.bestsellersLists.test()

        viewModel.message.test { awaitItem() shouldBe R.string.error_generic }
    }

    @Test
    fun `display loading indicator when fetching books`() {
        val testObserver = viewModel.isLoading.test()

        viewModel.bestsellersLists.test()

        testObserver.assertValueHistory(false, true, false)
    }
}

class FakeBooksApi : BooksApi {
    var response = BestsellersListsResponse(
        BestsellersListsResponse.Results(listOf(fakeBestsellersList))
    )
    var fail = false

    override suspend fun getBestsellersLists(): BestsellersListsResponse {
        if (fail) throw IOException()
        return response
    }

    override suspend fun getBestsellersListById(listId: String): BestsellersListResponse {
        TODO("Not yet implemented")
    }
}

val fakeBook = Book("fake isbn", "fake title", "fake contributor", "fake description", "fake cover")
val fakeBestsellersList = BestsellersList("fake id", "Fake name", listOf(fakeBook))
