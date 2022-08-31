package io.bucketeer.sdk.android.internal.evaluation

import android.annotation.SuppressLint
import android.content.SharedPreferences
import io.bucketeer.sdk.android.internal.Constants
import io.bucketeer.sdk.android.internal.evaluation.db.CurrentEvaluationDao
import io.bucketeer.sdk.android.internal.evaluation.db.LatestEvaluationDao
import io.bucketeer.sdk.android.internal.logd
import io.bucketeer.sdk.android.internal.loge
import io.bucketeer.sdk.android.internal.model.Evaluation
import io.bucketeer.sdk.android.internal.model.User
import io.bucketeer.sdk.android.internal.model.UserEvaluationsState
import io.bucketeer.sdk.android.internal.remote.ApiClient
import io.bucketeer.sdk.android.internal.remote.GetEvaluationsResult
import java.util.concurrent.Executor

internal class EvaluationInteractor(
  private val apiClient: ApiClient,
  private val currentEvaluationDao: CurrentEvaluationDao,
  private val latestEvaluationDao: LatestEvaluationDao,
  private val sharedPrefs: SharedPreferences,
  private val executor: Executor,
) {
  // key: userId
  private val latestEvaluations = mutableMapOf<String, List<Evaluation>>()
  private val currentEvaluations = mutableMapOf<String, List<Evaluation>>()

  private var currentEvaluationsId: String
    get() = sharedPrefs.getString(Constants.PREFERENCE_KEY_USER_EVALUATION_ID, "") ?: ""
    @SuppressLint("ApplySharedPref")
    set(value) {
      sharedPrefs.edit()
        .putString(Constants.PREFERENCE_KEY_USER_EVALUATION_ID, value)
        .commit()
    }

  @Suppress("MoveVariableDeclarationIntoWhen")
  fun fetch(user: User): GetEvaluationsResult {
    val currentEvaluationsId = this.currentEvaluationsId

    val result = apiClient.fetchEvaluations(user, currentEvaluationsId)

    when (result) {
      is GetEvaluationsResult.Success -> {
        val response = result.value.data
        val newEvaluationsId = response.user_evaluations_id
        if (currentEvaluationsId == newEvaluationsId) {
          logd { "Nothing to sync" }
          return result
        }

        val newEvaluations = response.evaluations.evaluations

        when (response.state) {
          UserEvaluationsState.FULL -> {
            val failed = latestEvaluationDao.deleteAllAndInsert(user.id, newEvaluations)
            if (failed) {
              loge { "Failed to update latest evaluations" }
              return result
            }

            this.currentEvaluationsId = newEvaluationsId

            val featureIds = newEvaluations.map { it.feature_id }
            currentEvaluationDao.deleteNotIn(user.id, featureIds)
            val newCurrentEvaluations = currentEvaluationDao.getEvaluations(user.id)

            latestEvaluations[user.id] = newEvaluations
            currentEvaluations[user.id] = newCurrentEvaluations
          }
          UserEvaluationsState.PARTIAL -> {
            latestEvaluationDao.put(user.id, newEvaluations)
            latestEvaluations[user.id] = latestEvaluationDao.get(user.id)
          }
          UserEvaluationsState.QUEUED -> {
            // no-op
          }
        }
      }
      is GetEvaluationsResult.Failure -> {
        logd(result.error) { "ApiError: ${result.error.message}" }
      }
    }
    return result
  }

  fun getLatest(userId: String, featureId: String): Evaluation? {
    val evaluations = latestEvaluations[userId] ?: emptyList()
    return evaluations.firstOrNull { it.feature_id == featureId }
  }

  fun getLatestAndRefreshCurrent(userId: String, featureId: String): Evaluation? {
    val evaluation = getLatest(userId, featureId) ?: return null

    executor.execute {
      currentEvaluationDao.upsertEvaluation(evaluation)
      val newCurrentEvaluation = currentEvaluationDao.getEvaluations(userId)
      currentEvaluations[userId] = newCurrentEvaluation
    }

    return evaluation
  }
}
