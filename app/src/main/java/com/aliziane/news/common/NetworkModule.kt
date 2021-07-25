package com.aliziane.news.common

import com.aliziane.news.home.StoriesApi
import com.aliziane.news.articledetails.CommunityApi
import com.aliziane.news.articlesearch.SearchApi
import com.aliziane.news.books.BooksApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
class NetworkModule {
    @ExperimentalSerializationApi
    @Singleton
    @Provides
    fun provideRetrofit(apiKeyInterceptor: ApiKeyInterceptor): Retrofit {
        val loggingInterceptor =
            HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()

        val json = Json { ignoreUnknownKeys = true }
        val contentType = MediaType.get("application/json")

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Singleton
    @Provides
    fun provideStoriesApi(retrofit: Retrofit): StoriesApi = retrofit.create(StoriesApi::class.java)

    @Singleton
    @Provides
    fun provideCommunityApi(retrofit: Retrofit): CommunityApi =
        retrofit.create(CommunityApi::class.java)

    @Singleton
    @Provides
    fun provideSearchApi(retrofit: Retrofit): SearchApi = retrofit.create(SearchApi::class.java)

    @Singleton
    @Provides
    fun provideBooksApi(retrofit: Retrofit): BooksApi = retrofit.create(BooksApi::class.java)

    companion object {
        private const val BASE_URL = "https://api.nytimes.com/svc/"
    }
}