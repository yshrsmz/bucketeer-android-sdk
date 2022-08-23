package io.bucketeer.sdk.android

import android.app.Application
import android.content.Context
import io.bucketeer.sdk.android.internal.di.DataModule
import io.bucketeer.sdk.android.internal.di.InteractorModule
import io.bucketeer.sdk.android.internal.evaluation.getVariationValue
import io.bucketeer.sdk.android.internal.logd
import io.bucketeer.sdk.android.internal.remote.GetEvaluationsResult
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
  private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(),
  private val dataModule: DataModule = DataModule(
    application = context.applicationContext as Application,
    apiKey = config.apiKey,
    endpoint = config.endpoint,
    featureTag = config.featureTag
  ),
  private val interactorModule: InteractorModule = InteractorModule(
    dataModule = dataModule,
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
    val user = userHolder.get()
    val featureTag = config.featureTag
    executor.execute {
      interactorModule.eventInteractor.trackGoalEvent(
        featureTag = featureTag,
        user = user,
        goalId = goalId,
        value = value,
      )
    }
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
      val result = interactorModule.evaluationInteractor.fetch(user = userHolder.get())

      executor.execute {
        val interactor = interactorModule.eventInteractor
        when (result) {
          is GetEvaluationsResult.Success -> {
            interactor.trackFetchEvaluationsSuccess(
              featureTag = result.featureTag,
              mills = result.millis,
              sizeByte = result.sizeByte,
              state = result.state
            )
          }
          is GetEvaluationsResult.Failure -> {
            interactor.trackFetchEvaluationsFailure(
              featureTag = result.featureTag,
              error = result.error
            )
          }
        }
      }
    }
  }

  // should we return Future?
  override fun flush() {
    executor.execute {
      interactorModule.eventInteractor.sendEvents(force = true)
    }
  }

  override fun evaluationDetails(featureId: String): BKTEvaluation? {
    val raw = interactorModule.evaluationInteractor
      .getLatestAndRefreshCurrent(userHolder.userId, featureId) ?: return null

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
      .getLatestAndRefreshCurrent(userHolder.userId, featureId)

    val user = userHolder.get()
    val featureTag = config.featureTag
    if (raw != null) {
      executor.execute {
        interactorModule.eventInteractor.trackEvaluationEvent(
          featureTag = featureTag,
          user = user,
          evaluation = raw
        )
      }
    } else {
      executor.execute {
        interactorModule.eventInteractor.trackDefaultEvaluationEvent(
          featureTag = featureTag,
          user = user,
          featureId = featureId
        )
      }
    }

    return raw.getVariationValue(defaultValue)
  }
}
