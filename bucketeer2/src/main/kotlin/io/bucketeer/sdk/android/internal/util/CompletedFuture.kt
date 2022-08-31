package io.bucketeer.sdk.android.internal.util

import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * Simple CompletableFuture alternative implementation to indicate `completed` state.
 */
class CompletedFuture<V>(
  private val value: V
) : Future<V> {
  override fun cancel(p0: Boolean): Boolean {
    return false
  }

  override fun isCancelled(): Boolean {
    return false
  }

  override fun isDone(): Boolean {
    return true
  }

  override fun get(): V {
    return value
  }

  override fun get(p0: Long, p1: TimeUnit?): V {
    return value
  }
}
