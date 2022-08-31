package io.bucketeer.sdk.android.internal.remote

import io.bucketeer.sdk.android.BKTException
import okhttp3.Response
import kotlin.contracts.ExperimentalContracts

fun Response.toBKTException(): BKTException? {
  if (isSuccessful) return null

  return when {
    (100..199).contains(code) -> BKTException.UnknownException(null)
    // success
    (200..299).contains(code) -> null
    // redirect
    (300..399).contains(code) -> BKTException.UnknownException(null)
    // client error
    401 == code -> BKTException.ApiUnauthenticatedException(null)
    404 == code -> BKTException.ApiInvocationFailException(
      "Some requested entity was not found.",
      null
    )
    (400..499).contains(code) -> BKTException.ApiInvocationFailException(
      "Client specified an invalid argument.",
      null
    )
    // server error
    503 == code -> BKTException.ApiUnavailableException(null)
    504 == code -> BKTException.ApiInvocationFailException(
      "Deadline expired before operation could complete.",
      null
    )
    (500..599).contains(code) -> BKTException.ApiServerException(null)
    else -> BKTException.UnknownException(null)
  }
}

@OptIn(ExperimentalContracts::class)
inline fun <T> measureTimeMillisWithResult(block: () -> T): Pair<Long, T> {
  kotlin.contracts.contract {
    callsInPlace(block, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
  }
  val start = System.currentTimeMillis()
  val result = block()
  return (System.currentTimeMillis() - start) to result
}

