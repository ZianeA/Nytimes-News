package com.aliziane.news

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
    fun provideApi(retrofit: Retrofit): NyTimesApi = retrofit.create(NyTimesApi::class.java)

    companion object {
        private const val BASE_URL = "https://api.nytimes.com/svc/"
    }
}