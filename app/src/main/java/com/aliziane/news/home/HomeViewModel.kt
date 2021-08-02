package com.aliziane.news.home

import androidx.lifecycle.*
import com.aliziane.news.R
import com.aliziane.news.common.DispatcherProvider
import com.aliziane.news.common.encodeToString
import com.aliziane.news.common.runAndCatch
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val api: StoriesApi,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    val articles = liveData(dispatcherProvider.main) {
        runAndCatch {
            _isLoading.value = true
            api.getTopStories().stories
        }
            .also { _isLoading.value = false }
            .onSuccess { emit(it) }
            .onFailure { _message.send(R.string.error_generic) }
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _navigateToArticleDetails = Channel<String>(Channel.BUFFERED)
    val navigateToArticleDetails = _navigateToArticleDetails.receiveAsFlow()

    private val _message = Channel<Int>(Channel.BUFFERED)
    val message = _message.receiveAsFlow()

    fun onArticleClick(article: Article) {
        viewModelScope.launch(dispatcherProvider.main) {
            _navigateToArticleDetails.send(article.encodeToString())
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): HomeViewModel
    }
}