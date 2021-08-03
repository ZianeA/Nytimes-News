package com.aliziane.news.articledetails

import androidx.lifecycle.*
import com.aliziane.news.*
import com.aliziane.news.common.DispatcherProvider
import com.aliziane.news.common.decodeFromString
import com.aliziane.news.common.requireArgument
import com.aliziane.news.common.runAndCatch
import com.aliziane.news.home.Article
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class ArticleDetailsViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val communityApi: CommunityApi,
    dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val _article = savedStateHandle.getLiveData<String>(ARTICLE_KEY)
        .map { it.decodeFromString<Article>() }

    val article: LiveData<Article> get() = _article

    val comments = liveData(dispatcherProvider.main) {
        val article =
            savedStateHandle.requireArgument<String>(ARTICLE_KEY).decodeFromString<Article>()

        runAndCatch {
            _isLoading.value = true
            kotlinx.coroutines.delay(2000)
            communityApi.getComments(article.url, CommunityApi.Sort.NEWEST).toComments()
        }
            .also { _isLoading.value = false }
            .onSuccess { emit(it) }
            .onFailure { _message.send(R.string.error_generic) }
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _message = Channel<Int>(Channel.BUFFERED)
    val message = _message.receiveAsFlow()

    private val _sort = MutableLiveData(R.string.sort_by_new)
    val sort: LiveData<Int> get() = _sort

    companion object {
        private const val ARTICLE_KEY = "article"
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): ArticleDetailsViewModel
    }
}