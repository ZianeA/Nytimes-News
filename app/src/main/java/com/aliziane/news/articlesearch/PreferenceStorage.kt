package com.aliziane.news.articlesearch

import android.app.Application
import dagger.Binds
import dagger.Module
import hu.autsoft.krate.SimpleKrate
import hu.autsoft.krate.intPref
import hu.autsoft.krate.stringSetPref
import javax.inject.Inject
import javax.inject.Singleton

interface PreferenceStorage {
    var searchHistory: Set<String>
}

@Singleton
class KratePreferenceStorage @Inject constructor(app: Application) : SimpleKrate(app),
    PreferenceStorage {

    override var searchHistory: Set<String> by stringSetPref(KEY_SEARCH_HISTORY, emptySet())

    companion object {
        const val KEY_SEARCH_HISTORY = "search_history"
    }
}

@Module
interface PreferenceStorageModule {
    @Binds
    fun bindKrate(krate: KratePreferenceStorage): PreferenceStorage
}