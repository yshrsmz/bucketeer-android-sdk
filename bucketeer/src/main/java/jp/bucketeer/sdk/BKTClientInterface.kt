package jp.bucketeer.sdk

import org.json.JSONObject

interface BKTClientInterface {

  fun getStringVariation(featureId: String, defaultValue: String): String

  fun getIntVariation(featureId: String, defaultValue: Int): Int

  fun getDoubleVariation(featureId: String, defaultValue: Double): Double

  fun getBooleanVariation(featureId: String, defaultValue: Boolean): Boolean

  fun getJsonVariation(featureId: String, defaultValue: JSONObject): JSONObject

  fun track(goalId: String, value: Double = 0.0)

  fun getUser(): BKTUser

  fun setUserAttributes(attributes: Map<String, String>)

  fun fetchEvaluations(
    fetchEvaluationsCallback: FetchEvaluationsCallback? = null
  )

  fun flush()

  fun getEvaluationDetails(featureId: String): Evaluation?

  interface FetchEvaluationsCallback {
    fun onSuccess()
    fun onError(exception: BKTException)
  }
}
