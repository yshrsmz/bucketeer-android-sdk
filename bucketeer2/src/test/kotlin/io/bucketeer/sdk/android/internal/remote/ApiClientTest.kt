package io.bucketeer.sdk.android.internal.remote

import com.google.common.truth.Truth.assertThat
import io.bucketeer.sdk.android.BKTUser
import io.bucketeer.sdk.android.internal.di.DataModule
import io.bucketeer.sdk.android.internal.model.UserEvaluationsState
import io.bucketeer.sdk.android.toRequest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class ApiClientTest {

  lateinit var server: MockWebServer
  lateinit var client: ApiClient
  lateinit var endpoint: String

  @Before
  fun setup() {
    server = MockWebServer()
    endpoint = server.url("").toString()
  }

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun fetchEvaluations() {
    val json = this::class.java.classLoader?.getResource("json/evaluations.json")?.readText()!!

    server.enqueue(MockResponse().setBody(json))

    client = ApiClientImpl(
      endpoint = endpoint,
      apiKey = "this_is_api_key",
      featureTag = "android",
      moshi = DataModule.createMoshi()
    )

    val user = BKTUser.Builder()
      .id("this_is_user_id")
      .customAttributes(mapOf("foo" to "bar"))
      .build()
      .toRequest()

    val result = client.fetchEvaluations(
      user = user,
      userEvaluationsId = "this_is_user_evaluations_id"
    )

    assertThat(result).isInstanceOf(GetEvaluationsResult.Success::class.java)

    val response = requireNotNull((result as GetEvaluationsResult.Success).value)

    assertThat(response.data.state).isEqualTo(UserEvaluationsState.FULL)

    val request = server.takeRequest()

    assertThat(request.path).isEqualTo("/v1/gateway/evaluations")
    assertThat(request.headers["Authorization"]).isEqualTo("this_is_api_key")
    println(request.body.readUtf8())
  }
}
