package com.aliziane.news.common

import android.app.Application
import com.aliziane.news.home.HomeFragment
import com.aliziane.news.articledetails.ArticleDetailsFragment
import com.aliziane.news.articlesearch.SearchViewModel
import com.aliziane.news.books.BooksViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(homeFragment: HomeFragment)
    fun inject(articleDetailsFragment: ArticleDetailsFragment)

    val searchViewModel: Provider<SearchViewModel.Factory>
    val booksViewModel: Provider<BooksViewModel>

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): AppComponent
    }
}