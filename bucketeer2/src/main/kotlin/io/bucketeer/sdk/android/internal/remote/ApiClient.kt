package io.bucketeer.sdk.android.internal.remote

import com.squareup.moshi.Moshi
import io.bucketeer.sdk.android.internal.logd
import io.bucketeer.sdk.android.internal.model.Event
import io.bucketeer.sdk.android.internal.model.User
import io.bucketeer.sdk.android.internal.model.request.GetEvaluationsRequest
import io.bucketeer.sdk.android.internal.model.request.RegisterEventsRequest
import io.bucketeer.sdk.android.internal.model.response.GetEvaluationsResponse
import io.bucketeer.sdk.android.internal.model.response.RegisterEventsResponse
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

interface ApiClient {
  fun fetchEvaluations(
    user: User,
    userEvaluationsId: String
  ): GetEvaluationsResult

  fun registerEvents(events: List<Event>): RegisterEventsResult
}

internal class ApiClientImpl(
  endpoint: String,
  private val apiKey: String,
  private val featureTag: String,
  private val moshi: Moshi
) : ApiClient {

  private val endpoint = endpoint.toHttpUrl()

  private val client: OkHttpClient = OkHttpClient.Builder()
    .build()

  override fun fetchEvaluations(
    user: User,
    userEvaluationsId: String
  ): GetEvaluationsResult {
    val body = GetEvaluationsRequest(
      tag = featureTag,
      user = user,
      user_evaluations_id = userEvaluationsId
    )

    val request = Request.Builder()
      .url(
        endpoint.newBuilder()
          .addPathSegments("v1/gateway/evaluations")
          .build()
      )
      .applyHeaders()
      .post(body = body.toJsonRequestBody())
      .build()

    val result = client.newCall(request).runCatching {
      logd { "--> Fetch Evaluation\n$body" }

      val (millis, response) = measureTimeMillisWithResult { execute() }
      if (!response.isSuccessful) {
        throw response.toBKTException()!!
      }

      val result = requireNotNull(response.fromJson<GetEvaluationsResponse>())

      logd { "--> END Fetch Evaluation" }
      logd { "<-- Fetch Evaluation\n$response\n<-- END Evaluation response" }

      GetEvaluationsResult.Success(
        value = result,
        millis = millis,
        sizeByte = response.body?.contentLength() ?: -1,
        featureTag = featureTag,
        state = result.data.state
      )
    }

    return result.fold(
      onSuccess = { res -> res },
      onFailure = { e -> GetEvaluationsResult.Failure(e, featureTag) }
    )
  }

  override fun registerEvents(events: List<Event>): RegisterEventsResult {
    val body = RegisterEventsRequest(
      events = events
    )

    val request = Request.Builder()
      .url(
        endpoint.newBuilder()
          .addPathSegments("v1/gateway/events")
          .build()
      )
      .applyHeaders()
      .post(body = body.toJsonRequestBody())
      .build()

    val result = client.newCall(request).runCatching {
      logd { "--> Register events\n$body" }
      val response = execute()

      if (!response.isSuccessful) {
        val e = response.toBKTException()!!
        logd(throwable = e) { "<-- Register events error" }
        throw e
      }

      val result = requireNotNull(response.fromJson<RegisterEventsResponse>())

      logd { "--> END Register events" }
      logd { "<-- Register events\n$result\n<-- END Register events" }

      RegisterEventsResult.Success(
        value = result
      )
    }

    return result.fold(
      onSuccess = { res -> res },
      onFailure = { e -> RegisterEventsResult.Failure(e) }
    )
  }

  private inline fun <reified T> T.toJson(): String {
    return moshi.adapter(T::class.java).toJson(this)
  }

  private inline fun <reified T> Response.fromJson(): T? {
    val adapter = moshi.adapter(T::class.java)
    return adapter.fromJson(this.body!!.source())
  }

  private inline fun <reified T> T.toJsonRequestBody(): RequestBody {
    return this.toJson()
      .toRequestBody("application/json".toMediaTypeOrNull())
  }

  private fun Request.Builder.applyHeaders(): Request.Builder {
    return this.header("Authorization", apiKey)
      .header("Content-Type", "application/json")
  }
}
