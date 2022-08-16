package io.bucketeer.sdk.android

import android.app.Application
import android.content.Context
import io.bucketeer.sdk.android.internal.di.DataModule
import io.bucketeer.sdk.android.internal.di.InteractorModule
import io.bucketeer.sdk.android.internal.evaluation.getVariationValue
import io.bucketeer.sdk.android.internal.logd
import io.bucketeer.sdk.android.internal.user.UserHolder
import io.bucketeer.sdk.android.internal.user.toBKTUser
import io.bucketeer.sdk.android.internal.user.toUser
import org.json.JSONObject
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService

internal class BKTClientImpl(
  private val context: Context,
  private val config: BKTConfig,
  user: BKTUser,
  private val userHolder: UserHolder = UserHolder(user.toUser()),
  private val dataModule: DataModule = DataModule(
    application = context.applicationContext as Application,
    apiKey = config.apiKey,
    endpoint = config.endpoint,
    featureTag = config.featureTag
  ),
  private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
  private val interactorModule: InteractorModule = InteractorModule(
    apiClient = dataModule::api,
    currentEvaluationDao = dataModule::currentEvaluationDao,
    latestEvaluationDao = dataModule::latestEvaluationDao,
    sharedPrefs = dataModule::sharedPreferences,
    executor = { executor }
  ),
) : BKTClient {

  override fun stringVariation(featureId: String, defaultValue: String): String {
    return getVariationValue(featureId, defaultValue)
  }

  override fun intVariation(featureId: String, defaultValue: Int): Int {
    return getVariationValue(featureId, defaultValue)
  }

  override fun doubleVariation(featureId: String, defaultValue: Double): Double {
    return getVariationValue(featureId, defaultValue)
  }

  override fun booleanVariation(featureId: String, defaultValue: Boolean): Boolean {
    return getVariationValue(featureId, defaultValue)
  }

  override fun jsonVariation(featureId: String, defaultValue: JSONObject): JSONObject {
    return getVariationValue(featureId, defaultValue)
  }

  override fun track(goalId: String, value: Double) {
    TODO("Not yet implemented")
  }

  override fun currentUser(): BKTUser {
    return userHolder.get().toBKTUser()
  }

  override fun setUserAttributes(attributes: Map<String, String>) {
    userHolder.update { it.copy(data = attributes) }
  }

  override fun fetchEvaluations(callback: BKTClient.FetchEvaluationsCallback?) {
    TODO("Not yet implemented")
  }

  override fun fetchEvaluations(): Future<Unit> {
    return executor.submit<Unit> {
      interactorModule.evaluationInteractor.fetch(user = userHolder.get())
    }
  }

  override fun flush() {
    TODO("Not yet implemented")
  }

  override fun evaluationDetails(featureId: String): BKTEvaluation? {
    val raw = interactorModule.evaluationInteractor
      .getLatestAndRefreshCurrent(userHolder.get().id, featureId) ?: return null

    return BKTEvaluation(
      id = raw.id,
      featureId = raw.feature_id,
      featureVersion = raw.feature_version,
      userId = raw.user_id,
      variationId = raw.variation_id,
      variationValue = raw.variation_value,
      reason = raw.reason.type.value
    )
  }

  private inline fun <reified T : Any> getVariationValue(featureId: String, defaultValue: T): T {
    logd { "Bucketeer.getVariation(featureId = $featureId, defaultValue = $defaultValue) called" }

    val raw = interactorModule.evaluationInteractor
      .getLatestAndRefreshCurrent(userHolder.get().id, featureId)

    if (raw != null) {
      // TODO track evaluation event
    } else {
      // TODO: track default evaluation event
    }

    return raw.getVariationValue(defaultValue)
  }
}
