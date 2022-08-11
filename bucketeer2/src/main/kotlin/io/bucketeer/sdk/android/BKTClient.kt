package io.bucketeer.sdk.android

import android.content.Context
import org.json.JSONObject

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

class BKTClientImpl(
  private val context: Context,
  private val config: BKTConfig,
  private val user: BKTUser

) : BKTClient {
  override fun stringVariation(featureId: String, defaultValue: String): String {
    TODO("Not yet implemented")
  }

  override fun intVariation(featureId: String, defaultValue: Int): Int {
    TODO("Not yet implemented")
  }

  override fun doubleVariation(featureId: String, defaultValue: Double): Double {
    TODO("Not yet implemented")
  }

  override fun booleanVariation(featureId: String, defaultValue: Boolean): Boolean {
    TODO("Not yet implemented")
  }

  override fun jsonVariation(featureId: String, defaultValue: JSONObject): JSONObject {
    TODO("Not yet implemented")
  }

  override fun track(goalId: String, value: Double) {
    TODO("Not yet implemented")
  }

  override fun currentUser(): BKTUser {
    TODO("Not yet implemented")
  }

  override fun setUserAttributes(attributes: Map<String, String>) {
    TODO("Not yet implemented")
  }

  override fun fetchEvaluations(callback: BKTClient.FetchEvaluationsCallback?) {
    TODO("Not yet implemented")
  }

  override fun flush() {
    TODO("Not yet implemented")
  }

  override fun evaluationDetails(featureId: String): BKTEvaluation? {
    TODO("Not yet implemented")
  }
}
