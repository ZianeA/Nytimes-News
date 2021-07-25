package com.aliziane.news.home

import androidx.lifecycle.*
import com.aliziane.news.common.encodeToString
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val api: StoriesApi
) : ViewModel() {

    val posts = liveData(Dispatchers.IO) { emit(api.getTopStories().stories) }

    private val _navigateToArticleDetails = Channel<String>(Channel.BUFFERED)
    val navigateToArticleDetails = _navigateToArticleDetails.receiveAsFlow()

    fun onArticleClick(article: Article) {
        viewModelScope.launch { _navigateToArticleDetails.send(article.encodeToString()) }
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): HomeViewModel
    }
}