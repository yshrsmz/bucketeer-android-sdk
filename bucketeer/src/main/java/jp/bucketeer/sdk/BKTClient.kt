package jp.bucketeer.sdk

import android.app.Application
import jp.bucketeer.sdk.util.SingletonHolder
import org.json.JSONObject

class BKTClient private constructor(
  private val application: Application,
  private val config: BKTConfig,
  private val user: BKTUser
) : BKTClientInterface {

  init {
    // TODO
  }

  companion object :
    SingletonHolder<BKTClientInterface, Application, BKTConfig, BKTUser>(::BKTClient)

  override fun getStringVariation(featureId: String, defaultValue: String): String {
    TODO("Not yet implemented")
  }

  override fun getIntVariation(featureId: String, defaultValue: Int): Int {
    TODO("Not yet implemented")
  }

  override fun getDoubleVariation(featureId: String, defaultValue: Double): Double {
    TODO("Not yet implemented")
  }

  override fun getBooleanVariation(featureId: String, defaultValue: Boolean): Boolean {
    TODO("Not yet implemented")
  }

  override fun getJsonVariation(featureId: String, defaultValue: JSONObject): JSONObject {
    TODO("Not yet implemented")
  }

  override fun track(goalId: String, value: Double) {
    TODO("Not yet implemented")
  }

  override fun getUser(): BKTUser {
    return TODO("Not yet implemented")
  }

  override fun setUserAttributes(attributes: Map<String, String>) {
    TODO("Not yet implemented")
  }

  override fun fetchEvaluations(
    fetchUserEvaluationsCallback: BKTClientInterface.FetchEvaluationsCallback?
  ) {
    TODO("Not yet implemented")
  }

  override fun flush() {
    TODO("Not yet implemented")
  }

  override fun getEvaluationDetails(featureId: String): Evaluation? {
    TODO("Not yet implemented")
  }
}
