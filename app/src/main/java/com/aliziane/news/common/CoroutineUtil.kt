package com.aliziane.news.common

import kotlinx.coroutines.CancellationException

/**
 * Similar to [runCatching] but rethrows if [CancellationException]
 */
inline fun <R> runAndCatch(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        if (e is CancellationException) throw e
        Result.failure(e)
    }
}