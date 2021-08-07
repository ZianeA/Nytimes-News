package com.aliziane.news.common

import android.app.Application
import com.aliziane.news.home.HomeFragment
import com.aliziane.news.articledetails.ArticleDetailsViewModel
import com.aliziane.news.articlesearch.SearchViewModel
import com.aliziane.news.books.BooksViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class, DispatcherModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(homeFragment: HomeFragment)

    val articleDetailsViewModel: Provider<ArticleDetailsViewModel.Factory>
    val searchViewModel: Provider<SearchViewModel.Factory>
    val booksViewModel: Provider<BooksViewModel>

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}