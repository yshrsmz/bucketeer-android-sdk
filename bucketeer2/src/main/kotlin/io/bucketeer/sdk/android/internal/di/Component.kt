package io.bucketeer.sdk.android.internal.di

import io.bucketeer.sdk.android.internal.evaluation.EvaluationInteractor
import io.bucketeer.sdk.android.internal.event.EventInteractor
import java.util.concurrent.Executor

internal class Component(
  val dataModule: DataModule,
  val interactorModule: InteractorModule,
  val executor: Executor
) {
  val evaluationInteractor: EvaluationInteractor by lazy {
    interactorModule.evaluationInteractor(
      apiClient = dataModule.apiClient,
      currentEvaluationDao = dataModule.currentEvaluationDao,
      latestEvaluationDao = dataModule.latestEvaluationDao,
      sharedPreferences = dataModule.sharedPreferences,
      executor = executor
    )
  }

  val eventInteractor: EventInteractor by lazy {
    interactorModule.eventInteractor(
      apiClient = dataModule.apiClient,
      eventDao = dataModule.eventDao,
      clock = dataModule.clock,
      idGenerator = dataModule.idGenerator
    )
  }
}
