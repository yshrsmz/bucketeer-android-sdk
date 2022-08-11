package io.bucketeer.sdk.android.internal.evaluation

import io.bucketeer.sdk.android.internal.dispatcher.Dispatcher
import io.bucketeer.sdk.android.internal.evaluation.db.CurrentEvaluationDao
import io.bucketeer.sdk.android.internal.evaluation.dto.CurrentEvaluationListDataChangedAction
import io.bucketeer.sdk.android.internal.model.Evaluation
import io.bucketeer.sdk.android.internal.remote.ApiClient

internal class ClientInteractorActionCreator(
  private val dispatcher: Dispatcher,
  val api: ApiClient,
  private val currentEvaluationDao: CurrentEvaluationDao
) {

  fun saveCurrentEvaluation(currentUserId: String, evaluation: Evaluation) {
    currentEvaluationDao.upsertEvaluation(evaluation)

    val userEvaluations = currentEvaluationDao.getEvaluations(currentUserId)
    dispatcher.send(CurrentEvaluationListDataChangedAction(currentUserId, userEvaluations))
  }

  fun refreshCurrentEvaluation(currentUserId: String) {
    val userEvaluations = currentEvaluationDao.getEvaluations(currentUserId)
    dispatcher.send(CurrentEvaluationListDataChangedAction(currentUserId, userEvaluations))
  }
}
