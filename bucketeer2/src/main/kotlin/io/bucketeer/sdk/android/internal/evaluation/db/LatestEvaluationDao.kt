package io.bucketeer.sdk.android.internal.evaluation.db

import io.bucketeer.sdk.android.internal.model.Evaluation
import io.bucketeer.sdk.android.internal.model.User

internal interface LatestEvaluationDao {
  fun put(user: User, list: List<Evaluation>)
  fun get(user: User): List<Evaluation>
  fun deleteAllAndInsert(user: User, list: List<Evaluation>): Boolean
}
