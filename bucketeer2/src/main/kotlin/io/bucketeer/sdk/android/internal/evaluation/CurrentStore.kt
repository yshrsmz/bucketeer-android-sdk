package io.bucketeer.sdk.android.internal.evaluation

import io.bucketeer.sdk.android.internal.dispatcher.Dispatcher
import io.bucketeer.sdk.android.internal.evaluation.dto.CurrentEvaluationListDataChangedAction
import io.bucketeer.sdk.android.internal.model.Evaluation
import io.bucketeer.sdk.android.internal.util.ObservableField

internal class CurrentStore(dispatcher: Dispatcher) {
  val currentEvaluations: ObservableField<Map<String, List<Evaluation>>> =
    ObservableField(mapOf())

  init {
    dispatcher.addObserver { _, arg ->
      when (arg) {
        is CurrentEvaluationListDataChangedAction -> {
          val map = currentEvaluations.value.toMutableMap()
          map[arg.userId] = arg.evaluations
          currentEvaluations.value = map
        }
      }
    }
  }
}
