package io.bucketeer.sdk.android.internal.di

import android.content.SharedPreferences
import io.bucketeer.sdk.android.BKTConfig
import io.bucketeer.sdk.android.internal.Clock
import io.bucketeer.sdk.android.internal.IdGenerator
import io.bucketeer.sdk.android.internal.evaluation.EvaluationInteractor
import io.bucketeer.sdk.android.internal.evaluation.db.CurrentEvaluationDao
import io.bucketeer.sdk.android.internal.evaluation.db.LatestEvaluationDao
import io.bucketeer.sdk.android.internal.event.EventInteractor
import io.bucketeer.sdk.android.internal.event.db.EventDao
import io.bucketeer.sdk.android.internal.remote.ApiClient
import java.util.concurrent.Executor

internal class InteractorModule(
  private val config: BKTConfig
) {
  fun evaluationInteractor(
    apiClient: ApiClient,
    currentEvaluationDao: CurrentEvaluationDao,
    latestEvaluationDao: LatestEvaluationDao,
    sharedPreferences: SharedPreferences,
    executor: Executor
  ): EvaluationInteractor {
    return EvaluationInteractor(
      apiClient = apiClient,
      currentEvaluationDao = currentEvaluationDao,
      latestEvaluationDao = latestEvaluationDao,
      sharedPrefs = sharedPreferences,
      executor = executor
    )
  }

  fun eventInteractor(
    apiClient: ApiClient,
    eventDao: EventDao,
    clock: Clock,
    idGenerator: IdGenerator
  ): EventInteractor {
    return EventInteractor(
      eventsMaxBatchQueueCount = config.eventsMaxBatchQueueCount,
      apiClient = apiClient,
      eventDao = eventDao,
      clock = clock,
      idGenerator = idGenerator
    )
  }
}
