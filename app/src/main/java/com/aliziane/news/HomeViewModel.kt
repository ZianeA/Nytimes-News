package com.aliziane.news

import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class HomeViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val api: NyTimesApi
) : ViewModel() {

    val posts = liveData(Dispatchers.IO) { emit(api.getTopStories().stories) }

    @AssistedFactory
    interface Factory {
        fun create(savedStateHandle: SavedStateHandle): HomeViewModel
    }
}