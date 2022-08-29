package io.bucketeer.sdk.android.mocks

import io.bucketeer.sdk.android.internal.model.*
import java.util.*

val idGenerator: () -> String = { UUID.randomUUID().toString() }

val evaluationEvent1: Event by lazy {
  Event(
    id = "5ce0ae1a-8568-44d3-961b-89d7735f2a93",
    type = EventType.EVALUATION,
    event = EventData.EvaluationEvent(
      timestamp = 1661780821,
      feature_id = "evaluation1",
      user_id = user1.id,
      user = user1,
      reason = Reason(type = ReasonType.DEFAULT),
      tag = "",
      source_id = SourceID.ANDROID
    )
  )
}

val evaluationEvent2: Event by lazy {
  Event(
    id = "62d76a53-3396-4dfb-8dce-dd1b794a984d",
    type = EventType.EVALUATION,
    event = EventData.EvaluationEvent(
      timestamp = 1661780821,
      feature_id = "evaluation2",
      user_id = user1.id,
      user = user1,
      reason = Reason(type = ReasonType.DEFAULT),
      tag = "",
      source_id = SourceID.ANDROID
    )
  )
}

val goalEvent1: Event by lazy {
  Event(
    id = "408741bd-ae4c-45e9-888d-a85e88817fec",
    type = EventType.GOAL,
    event = EventData.GoalEvent(
      timestamp = 1661780821,
      goal_id = "goal1",
      user_id = user1.id,
      user = user1,
      value = 0.0,
      tag = "",
      source_id = SourceID.ANDROID
    )
  )
}

val goalEvent2: Event by lazy {
  Event(
    id = "5ea231b4-c3c7-4b9f-97a2-ee50337f51f0",
    type = EventType.GOAL,
    event = EventData.GoalEvent(
      timestamp = 1661780821,
      goal_id = "goal2",
      user_id = user1.id,
      user = user1,
      value = 0.0,
      tag = "",
      source_id = SourceID.ANDROID
    )
  )
}
