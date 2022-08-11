package io.bucketeer.sdk.android.internal.events

import io.bucketeer.sdk.android.internal.model.Evaluation
import io.bucketeer.sdk.android.internal.model.Event
import io.bucketeer.sdk.android.internal.model.User

val NANOS_PER_MILLISECOND: Long = 1000000
val MILLIS_PER_SECOND: Long = 1000

internal fun generateGoalEvent(
  tag: String,
  timestamp: Long,
  goalId: String,
  value: Double,
  user: User,
  evaluations: List<Evaluation>
): Event {
//  return EventOuterClass.GoalEvent.newBuilder().apply {
//    this.sourceId = EventOuterClass.SourceId.ANDROID
//    this.tag = tag
//    this.timestamp = timestamp
//    this.goalId = goalId
//    this.userId = user.id
//    this.value = value
//    this.user = user
//    addAllEvaluations(evaluations)
//  }.build()
  TODO()
}

internal fun generateEvaluationEvent(
  tag: String,
  timestamp: Long,
  evaluation: Evaluation,
  user: User
): Event {
//  return EventOuterClass.EvaluationEvent.newBuilder().apply {
//    this.sourceId = EventOuterClass.SourceId.ANDROID
//    this.tag = tag
//    this.timestamp = timestamp
//    this.featureId = evaluation.featureId
//    this.featureVersion = evaluation.featureVersion
//    this.userId = user.id
//    this.variationId = evaluation.variationId
//    this.user = user
//    this.reason = evaluation.reason
//  }.build()
  TODO()
}

internal fun generateDefaultEvaluationEvent(
  tag: String,
  timestamp: Long,
  user: User,
  featureId: String
): Event {
//  val reason = ReasonOuterClass.Reason.newBuilder()
//    .setType(ReasonOuterClass.Reason.Type.CLIENT)
//    .build()
//  return EventOuterClass.EvaluationEvent.newBuilder().apply {
//    this.sourceId = EventOuterClass.SourceId.ANDROID
//    this.tag = tag
//    this.timestamp = timestamp
//    this.featureId = featureId
//    this.userId = user.id
//    this.user = user
//    this.reason = reason
//  }.build()
  TODO()
}

internal fun generateGetEvaluationLatencyMetricsEvent(
  latencyMillis: Long,
  labels: Map<String, String>
): Event {
//  return EventOuterClass.GetEvaluationLatencyMetricsEvent.newBuilder().apply {
//    this.duration = Duration.newBuilder()
//      .setSeconds(latencyMillis / MILLIS_PER_SECOND)
//      .setNanos((latencyMillis % MILLIS_PER_SECOND * NANOS_PER_MILLISECOND).toInt())
//      .build()
//  }.putAllLabels(labels)
//    .build().toMetricsEvent()
  TODO()
}

internal fun generateGetEvaluationSizeMetricsEvent(
  sizeByte: Int,
  labels: Map<String, String>
): Event {
//  return EventOuterClass.GetEvaluationSizeMetricsEvent.newBuilder().apply {
//    this.sizeByte = sizeByte
//  }.putAllLabels(labels)
//    .build().toMetricsEvent()
  TODO()
}

internal fun generateInternalErrorCountMetricsEvent(
  tag: String
): Event {
//  return EventOuterClass.InternalErrorCountMetricsEvent.newBuilder().apply {
//    this.tag = tag
//  }.build().toMetricsEvent()
  TODO()
}

internal fun generateTimeoutErrorCountMetricsEvent(
  tag: String
): Event {
//  return EventOuterClass.TimeoutErrorCountMetricsEvent.newBuilder().apply {
//    this.tag = tag
//  }.build().toMetricsEvent()
  TODO()
}
