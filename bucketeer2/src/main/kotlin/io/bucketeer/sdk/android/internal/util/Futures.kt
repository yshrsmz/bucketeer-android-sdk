package io.bucketeer.sdk.android.internal.util

import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

object Futures {
  fun <V> success(value: V): Future<V> = SuccessFuture(value)
  fun <V> failure(error: Throwable): Future<V> = TODO()
}

internal class SuccessFuture<V>(
  private val value: V
) : Future<V> {
  override fun cancel(p0: Boolean): Boolean = false

  override fun isCancelled(): Boolean = false

  override fun isDone(): Boolean = true

  override fun get(): V = value

  override fun get(p0: Long, p1: TimeUnit?): V = value
}

internal class FailureFuture<V>(
  private val error: Throwable
) : Future<V> {
  override fun cancel(p0: Boolean): Boolean = false

  override fun isCancelled(): Boolean = false

  override fun isDone(): Boolean = true

  override fun get(): V = throw ExecutionException(error)

  override fun get(p0: Long, p1: TimeUnit?): V = throw ExecutionException(error)
}
