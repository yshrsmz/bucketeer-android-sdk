package io.bucketeer.sdk.android.internal.evaluation

import io.bucketeer.sdk.android.internal.dispatcher.Dispatcher
import io.bucketeer.sdk.android.internal.evaluation.dto.LatestEvaluationChangedAction
import io.bucketeer.sdk.android.internal.evaluation.dto.RefreshManuallyStateChangedAction
import io.bucketeer.sdk.android.internal.model.Evaluation
import io.bucketeer.sdk.android.internal.util.ObservableField

internal class LatestEvaluationStore(val dispatcher: Dispatcher) {
  val latestEvaluations: ObservableField<MutableMap<String, List<Evaluation>>> =
    ObservableField(mutableMapOf())
  val refreshManuallyState: ObservableField<RefreshManuallyStateChangedAction.State> =
    ObservableField(RefreshManuallyStateChangedAction.State.Loading)

  init {
    dispatcher.addObserver { _, action ->
      when (action) {
        is LatestEvaluationChangedAction -> {
          latestEvaluations.value[action.user.id] = action.latestEvaluation
          latestEvaluations.value = latestEvaluations.value
        }
        is RefreshManuallyStateChangedAction -> {
          refreshManuallyState.value = action.state
        }
      }
    }
  }
}
