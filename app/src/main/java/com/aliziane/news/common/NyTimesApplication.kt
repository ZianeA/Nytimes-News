package com.aliziane.news.common

import android.app.Application
import com.aliziane.news.BuildConfig
import timber.log.Timber

class NyTimesApplication : Application() {
    val appComponent: AppComponent by lazy { DaggerAppComponent.factory().create(this) }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}