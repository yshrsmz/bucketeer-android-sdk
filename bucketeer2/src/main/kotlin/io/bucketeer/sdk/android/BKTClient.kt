package io.bucketeer.sdk.android

import android.content.Context
import org.json.JSONObject
import java.util.concurrent.Future

interface BKTClient {

  fun stringVariation(featureId: String, defaultValue: String): String

  fun intVariation(featureId: String, defaultValue: Int): Int

  fun doubleVariation(featureId: String, defaultValue: Double): Double

  fun booleanVariation(featureId: String, defaultValue: Boolean): Boolean

  fun jsonVariation(featureId: String, defaultValue: JSONObject): JSONObject

  fun track(goalId: String, value: Double)

  fun currentUser(): BKTUser

  fun setUserAttributes(attributes: Map<String, String>)

  fun fetchEvaluations(callback: FetchEvaluationsCallback?)

  fun fetchEvaluations(): Future<Unit>

  fun flush()

  fun evaluationDetails(featureId: String): BKTEvaluation?

  interface FetchEvaluationsCallback {
    fun onSuccess()
    fun onError(exception: BKTException)
  }

  companion object {
    @Volatile
    private var instance: BKTClient? = null

    fun getInstance(): BKTClient {
      return requireNotNull(instance) { "BKTClient is not initialized" }
    }

    @Suppress("unused")
    fun initialize(
      context: Context,
      config: BKTConfig,
      user: BKTUser,
      timeoutMillis: Long = 5000,
      callback: (isTimeout: Boolean) -> Unit
    ) {
      if (instance != null) {
        return
      }

      val client = BKTClientImpl(context, config, user)

//      client.fetchEvaluations()
    }
  }
}
