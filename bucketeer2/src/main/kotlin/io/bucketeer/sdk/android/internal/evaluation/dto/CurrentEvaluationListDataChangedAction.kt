package io.bucketeer.sdk.android.internal.evaluation.dto

import io.bucketeer.sdk.android.internal.model.Evaluation

internal data class CurrentEvaluationListDataChangedAction(
  val userId: String,
  val evaluations: List<Evaluation>
)
