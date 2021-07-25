package com.aliziane.news.articledetails

import androidx.lifecycle.*
import com.aliziane.news.*
import com.aliziane.news.common.decodeFromString
import com.aliziane.news.common.requireArgument
import com.aliziane.news.home.Article
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class ArticleDetailsViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val communityApi: CommunityApi
) : ViewModel() {

    private val _article = savedStateHandle.getLiveData<String>(ARTICLE_KEY)
        .map { it.decodeFromString<Article>() }

    val article: LiveData<Article> get() = _article

    val comments = liveData {
        val article =
            savedStateHandle.requireArgument<String>(ARTICLE_KEY).decodeFromString<Article>()
        val comments = communityApi.getComments(article.url, CommunityApi.Sort.NEWEST).toComments()
        emit(comments)
    }

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