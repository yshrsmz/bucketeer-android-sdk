package io.bucketeer.sdk.android.internal.evaluation.db

internal data class LatestEvaluationEntity(
  val userId: String,
  val evaluation: ByteArray
) {
  companion object {
    const val TABLE_NAME = "latest_evaluation"
    const val COLUMN_USER_ID = "user_id"
    const val COLUMN_FEATURE_ID = "feature_id"
    const val COLUMN_EVALUATION = "evaluation"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as LatestEvaluationEntity

    if (userId != other.userId) return false
    if (!evaluation.contentEquals(other.evaluation)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = userId.hashCode()
    result = 31 * result + evaluation.contentHashCode()
    return result
  }
}
