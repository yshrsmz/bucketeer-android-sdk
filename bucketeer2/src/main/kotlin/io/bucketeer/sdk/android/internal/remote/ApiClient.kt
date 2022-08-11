package io.bucketeer.sdk.android.internal.remote

import com.squareup.moshi.Moshi
import io.bucketeer.sdk.android.internal.model.User
import io.bucketeer.sdk.android.internal.model.request.GetEvaluationsRequest
import io.bucketeer.sdk.android.internal.model.request.RegisterEventsRequest
import io.bucketeer.sdk.android.internal.model.response.GetEvaluationResponse
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
  ): Result<GetEvaluationResponse>

  fun registerEvents(events: List<RegisterEventsRequest>)
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
  ): Result<GetEvaluationResponse> {
    val body = GetEvaluationsRequest(
      tag = featureTag,
      user = user
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

    return client.newCall(request).runCatching {
      val response = execute()
      if (!response.isSuccessful) {
        throw response.toBKTException()!!
      }

      requireNotNull(response.fromJson<GetEvaluationResponse>())
    }
  }

  override fun registerEvents(events: List<RegisterEventsRequest>) {
    TODO("Not yet implemented")
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
