package com.aliziane.news.articlesearch

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.aliziane.news.R
import com.aliziane.news.common.DispatcherProvider
import com.aliziane.news.common.encodeToString
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
    private val preferenceStorage: PreferenceStorage,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchSuggestions = savedStateHandle.getLiveData<String>(KEY_SUGGESTION_QUERY)
        .asFlow()
        .distinctUntilChanged()
        .debounce(500)
        .transformLatest<String, List<Article>> { query ->
            if (query.isBlank()) {
                emit(emptyList())
            } else {
                runAndCatch {
                    _isLoading.value = true
                    api.search(query).toArticles()
                }
                    .also { _isLoading.value = false }
                    .onSuccess { emit(it) }
                    .onFailure { _message.send(R.string.error_generic) }
            }
        }
        .asLiveData(dispatcherProvider.main)

    private val _searchHistory = MutableLiveData(preferenceStorage.searchHistory)
    val searchHistory: LiveData<Set<String>> get() = _searchHistory

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

    private val _navigateToArticleDetails = Channel<String>(Channel.BUFFERED)
    val navigateToArticleDetails = _navigateToArticleDetails.receiveAsFlow()

    fun onQueryChange(query: String?) {
        savedStateHandle.set(KEY_SUGGESTION_QUERY, query.orEmpty())

        if (query.isNullOrBlank()) {
            _searchHistory.value = preferenceStorage.searchHistory
        } else {
            _searchHistory.value = _searchHistory.value!!.filter { it.contains(query) }.toSet()
        }
    }

    fun onQuerySubmit(query: String?) {
        savedStateHandle.set(KEY_RESULT_QUERY, query.orEmpty())
        if (!query.isNullOrBlank()) {
            if (preferenceStorage.searchHistory.size >= SEARCH_HISTORY_CAPACITY) {
                preferenceStorage.searchHistory -= preferenceStorage.searchHistory.first()
            }
            preferenceStorage.searchHistory += query
            _searchHistory.value = preferenceStorage.searchHistory
        }
    }

    fun onArticleClick(article: Article) {
        viewModelScope.launch(dispatcherProvider.main) {
            _navigateToArticleDetails.send(article.encodeToString())
        }
    }

    fun onDeleteSearchHistoryItem(item: String) {
        preferenceStorage.searchHistory -= item
        _searchHistory.value = _searchHistory.value!! - item
    }

    companion object {
        private const val KEY_RESULT_QUERY = "result_query"
        private const val KEY_SUGGESTION_QUERY = "suggestion_query"
        private const val SEARCH_HISTORY_CAPACITY = 5
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): SearchViewModel
    }
}