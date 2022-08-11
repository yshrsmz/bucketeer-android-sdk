package io.bucketeer.sdk.android.internal.evaluation

import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import io.bucketeer.sdk.android.BKTException
import io.bucketeer.sdk.android.internal.Constants
import io.bucketeer.sdk.android.internal.dispatcher.Dispatcher
import io.bucketeer.sdk.android.internal.evaluation.db.CurrentEvaluationDao
import io.bucketeer.sdk.android.internal.evaluation.db.LatestEvaluationDao
import io.bucketeer.sdk.android.internal.evaluation.dto.CurrentEvaluationListDataChangedAction
import io.bucketeer.sdk.android.internal.evaluation.dto.LatestEvaluationChangedAction
import io.bucketeer.sdk.android.internal.evaluation.dto.RefreshManuallyStateChangedAction
import io.bucketeer.sdk.android.internal.logd
import io.bucketeer.sdk.android.internal.loge
import io.bucketeer.sdk.android.internal.model.User
import io.bucketeer.sdk.android.internal.model.UserEvaluationsState
import io.bucketeer.sdk.android.internal.remote.ApiClient

internal class LatestEvaluationActionCreator(
  private val dispatcher: Dispatcher,
  val api: ApiClient,
  private val latestEvaluationDao: LatestEvaluationDao,
  private val currentEvaluationDao: CurrentEvaluationDao,
  private val sharePref: SharedPreferences
) {

  @VisibleForTesting
  var currentUserEvaluationsId: String = sharePref.getString(
    Constants.PREFERENCE_KEY_USER_EVALUATION_ID,
    ""
  ) ?: ""

  fun refreshLatestEvaluationManuallyFromApi(user: User) {
    dispatcher.send(
      RefreshManuallyStateChangedAction(RefreshManuallyStateChangedAction.State.Loading)
    )
    val result = refreshLatestEvaluationFromApi(user)
    val state = when {
      result.isSuccess -> RefreshManuallyStateChangedAction.State.Loaded
      else -> RefreshManuallyStateChangedAction.State.Error(result.exceptionOrNull()!! as BKTException)
    }
    dispatcher.send(RefreshManuallyStateChangedAction(state))
  }

  fun refreshLatestEvaluationFromApi(user: User): Result<*> {
    refreshLatestEvaluationFromDao(user)
    val currentUserEvaluationId = this.currentUserEvaluationsId
    val result = api.fetchEvaluations(user, currentUserEvaluationId)

    when {
      result.isSuccess -> {
        run {
          val response = result.getOrThrow().data
          val userEvaluationsId = response.user_evaluations_id
          // Do nothing in case of there are no changes in the user evaluations
          if (currentUserEvaluationId == userEvaluationsId) {
            logd { "Nothing to sync" }
            return@run
          }
          val list = response.evaluations.evaluations
          when (response.state) {
            UserEvaluationsState.FULL -> {
              if (!latestEvaluationDao.deleteAllAndInsert(user, list)) {
                loge { "Failed to update latest evaluations" }
                return@run
              }
              updateUserEvaluationId(userEvaluationsId)
              val featureIds = list.map { it.feature_id }
              currentEvaluationDao.deleteNotIn(user.id, featureIds)
              val currentEvaluations = currentEvaluationDao.getEvaluations(user.id)

              dispatcher.send(LatestEvaluationChangedAction(user, list))
              dispatcher.send(CurrentEvaluationListDataChangedAction(user.id, currentEvaluations))
            }
            UserEvaluationsState.PARTIAL -> {
              latestEvaluationDao.put(user, list)
              dispatcher.send(LatestEvaluationChangedAction(user, latestEvaluationDao.get(user)))
            }
            UserEvaluationsState.QUEUED,
            null -> {
              // do nothing
            }
            else -> {
              // do nothing
            }
          }
        }
      }
      result.isFailure -> {
        val e = result.exceptionOrNull()
        logd(throwable = e) { "ApiError: ${e?.message}" }
      }
    }
    return result
  }

  @VisibleForTesting
  fun updateUserEvaluationId(userEvaluationsId: String) {
    currentUserEvaluationsId = userEvaluationsId
    sharePref.edit().putString(
      Constants.PREFERENCE_KEY_USER_EVALUATION_ID,
      userEvaluationsId
    ).commit()
  }

  @VisibleForTesting
  fun refreshLatestEvaluationFromDao(user: User) {
    val latestEvaluationFromDao = latestEvaluationDao.get(user)
    dispatcher.send(LatestEvaluationChangedAction(user, latestEvaluationFromDao))
  }
}
