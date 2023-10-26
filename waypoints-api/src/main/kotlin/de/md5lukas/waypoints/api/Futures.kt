package de.md5lukas.waypoints.api

import java.util.concurrent.CompletableFuture
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future

inline fun <T> future(crossinline block: suspend () -> T): CompletableFuture<T> =
    CoroutineScope(EmptyCoroutineContext).future { block() }
