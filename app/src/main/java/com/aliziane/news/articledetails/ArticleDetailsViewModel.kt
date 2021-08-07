package com.aliziane.news.articledetails

import androidx.annotation.StringRes
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

    private val _article = savedStateHandle.getLiveData<String>(KEY_ARTICLE)
        .map { it.decodeFromString<Article>() }

    val article: LiveData<Article> get() = _article

    private val _sortBy = savedStateHandle.getLiveData(KEY_SORT_BY, SortBy.NEW)
    val sortBy: LiveData<SortBy> get() = _sortBy

    val comments = _sortBy.switchMap { sortBy ->
        liveData(dispatcherProvider.main) {
            val article =
                savedStateHandle.requireArgument<String>(KEY_ARTICLE).decodeFromString<Article>()

            runAndCatch {
                _isLoading.value = true
                communityApi.getComments(article.url, sortBy.sort).toComments()
            }
                .also { _isLoading.value = false }
                .onSuccess { emit(it) }
                .onFailure { _message.send(R.string.error_generic) }
        }
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _message = Channel<Int>(Channel.BUFFERED)
    val message = _message.receiveAsFlow()

    fun onSortByChange(sortBy: SortBy) {
        _sortBy.value = sortBy
    }

    companion object {
        private const val KEY_ARTICLE = "article"
        private const val KEY_SORT_BY = "sort_by"
    }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): ArticleDetailsViewModel
    }
}

enum class SortBy(@StringRes val displayName: Int, val sort: CommunityApi.Sort) {
    NEW(R.string.sort_by_new, CommunityApi.Sort.NEWEST),
    OLD(R.string.sort_by_old, CommunityApi.Sort.OLDEST),
    TOP(R.string.sort_by_top, CommunityApi.Sort.READER)
}