package me.schulten.util

import com.github.benmanes.caffeine.cache.AsyncCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.future.future
import kotlinx.coroutines.supervisorScope

class SuspendingCache<K, V>(private val asyncCache: AsyncCache<K, V>) {

  suspend fun get(key: K, mappingFunction: suspend (K) -> V): V = supervisorScope {
    getAsync(key, mappingFunction).await()
  }

  private fun CoroutineScope.getAsync(key: K, mappingFunction: suspend (K) -> V) = asyncCache.get(key) { k, _ ->
    future {
      mappingFunction(k)
    }
  }
}