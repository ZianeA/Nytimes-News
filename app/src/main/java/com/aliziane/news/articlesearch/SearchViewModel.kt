package com.aliziane.news.articlesearch

import androidx.lifecycle.*
import com.aliziane.news.Article
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

class SearchViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val api: SearchApi
) : ViewModel() {
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchSuggestions = savedStateHandle.getLiveData<String>(KEY_SUGGESTION_QUERY)
        .asFlow()
        .filter { query -> query.isNotBlank() }
        .distinctUntilChanged()
        .debounce(500)
        .transformLatest<String, List<String>> { query ->
            val suggestions = api.search(query)
                .toArticles()
                .map { article -> article.title }

            emit(suggestions)
        }
        .asLiveData()

    val searchQuery = savedStateHandle.getLiveData(KEY_RESULT_QUERY, "")

    val searchResult = searchQuery
        .switchMap { query -> liveData { emit(api.search(query).toArticles()) } }

    fun onQueryChange(query: String?) {
        savedStateHandle.set(KEY_SUGGESTION_QUERY, query.orEmpty())
    }

    fun onQuerySubmit(query: String?) {
        savedStateHandle.set(KEY_RESULT_QUERY, query.orEmpty())
    }

    companion object {
        private const val KEY_RESULT_QUERY = "result_query"
        private const val KEY_SUGGESTION_QUERY = "suggestion_query"
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): SearchViewModel
    }
}