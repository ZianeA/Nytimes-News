package com.aliziane.news.common

import androidx.lifecycle.SavedStateHandle
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> T.encodeToString(): String {
    return Json.encodeToString(T::class.serializer(), this)
}

@OptIn(InternalSerializationApi::class)
inline fun <reified T : Any> String.decodeFromString(): T {
    return Json.decodeFromString(
        T::class.serializer(),
        requireNotNull(this)
    )
}

fun <T> SavedStateHandle.requireArgument(key: String): T = requireNotNull(this.get<T>(key))
