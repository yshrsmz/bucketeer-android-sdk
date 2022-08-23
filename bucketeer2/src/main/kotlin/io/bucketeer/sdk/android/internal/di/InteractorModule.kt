package io.bucketeer.sdk.android.internal.di

import io.bucketeer.sdk.android.internal.evaluation.EvaluationInteractor
import io.bucketeer.sdk.android.internal.event.EventInteractor
import java.util.concurrent.Executor

internal class InteractorModule(
  private val dataModule: DataModule,
  private val executor: () -> Executor,
) {
  val evaluationInteractor: EvaluationInteractor by lazy {
    EvaluationInteractor(
      apiClient = dataModule.api,
      currentEvaluationDao = dataModule.currentEvaluationDao,
      latestEvaluationDao = dataModule.latestEvaluationDao,
      sharedPrefs = dataModule.sharedPreferences,
      executor = executor()
    )
  }

  val eventInteractor: EventInteractor by lazy {
    EventInteractor(
      eventDao = dataModule.eventDao,
      clock = dataModule.clock,
      idGenerator = dataModule.idGenerator
    )
  }
}
