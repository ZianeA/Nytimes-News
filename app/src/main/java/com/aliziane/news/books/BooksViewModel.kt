package com.aliziane.news.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.aliziane.news.R
import com.aliziane.news.common.DispatcherProvider
import com.aliziane.news.common.runAndCatch
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

class BooksViewModel @Inject constructor(
    private val booksApi: BooksApi,
    dispatcherProvider: DispatcherProvider
) : ViewModel() {
    val bestsellersLists = liveData(dispatcherProvider.main) {
        runAndCatch {
            _isLoading.value = true
            booksApi.getBestsellersLists().toBestsellersLists()
        }
            .also { _isLoading.value = false }
            .onSuccess { emit(it) }
            .onFailure { _message.send(R.string.error_generic) }
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _message = Channel<Int>(Channel.BUFFERED)
    val message = _message.receiveAsFlow()
}