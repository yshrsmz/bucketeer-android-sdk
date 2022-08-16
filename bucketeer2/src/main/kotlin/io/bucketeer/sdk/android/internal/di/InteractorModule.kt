package io.bucketeer.sdk.android.internal.di

import android.content.SharedPreferences
import io.bucketeer.sdk.android.internal.evaluation.EvaluationInteractor
import io.bucketeer.sdk.android.internal.evaluation.db.CurrentEvaluationDao
import io.bucketeer.sdk.android.internal.evaluation.db.LatestEvaluationDao
import io.bucketeer.sdk.android.internal.remote.ApiClient
import java.util.concurrent.Executor

internal class InteractorModule(
  private val apiClient: () -> ApiClient,
  private val currentEvaluationDao: () -> CurrentEvaluationDao,
  private val latestEvaluationDao: () -> LatestEvaluationDao,
  private val sharedPrefs: () -> SharedPreferences,
  private val executor: () -> Executor,
) {
  internal val evaluationInteractor: EvaluationInteractor by lazy {
    EvaluationInteractor(
      apiClient = apiClient(),
      currentEvaluationDao = currentEvaluationDao(),
      latestEvaluationDao = latestEvaluationDao(),
      sharedPrefs = sharedPrefs(),
      executor = executor()
    )
  }
}
