package com.aliziane.news.articlesearch

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.aliziane.news.R
import com.aliziane.news.common.DispatcherProvider
import com.aliziane.news.common.runAndCatch
import com.aliziane.news.home.Article
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

class SearchViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val api: SearchApi,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchSuggestions = savedStateHandle.getLiveData<String>(KEY_SUGGESTION_QUERY)
        .asFlow()
        .distinctUntilChanged()
        .debounce(500)
        .transformLatest<String, List<String>> { query ->
            if (query.isBlank()) {
                emit(emptyList())
            } else {
                runAndCatch {
                    _isLoading.value = true
                    api.search(query).toArticles().map { article -> article.title }
                }
                    .also { _isLoading.value = false }
                    .onSuccess { emit(it) }
                    .onFailure { _message.send(R.string.error_generic) }
            }
        }
        .asLiveData(dispatcherProvider.main)

    val searchQuery = savedStateHandle.getLiveData(KEY_RESULT_QUERY, "")

    val searchResult = searchQuery
        .switchMap { query ->
            liveData {
                _isLoading.value = true
                runAndCatch { api.search(query).toArticles() }
                    .also { _isLoading.value = false }
                    .onSuccess { emit(it) }
                    .onFailure { _message.send(R.string.error_generic) }
            }
        }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _message = Channel<Int>(Channel.BUFFERED)
    val message = _message.receiveAsFlow()

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