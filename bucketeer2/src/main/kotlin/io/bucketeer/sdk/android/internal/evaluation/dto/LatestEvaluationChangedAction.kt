package io.bucketeer.sdk.android.internal.evaluation.dto

import io.bucketeer.sdk.android.internal.model.Evaluation
import io.bucketeer.sdk.android.internal.model.User

internal data class LatestEvaluationChangedAction(
  val user: User,
  val latestEvaluation: List<Evaluation>
)
