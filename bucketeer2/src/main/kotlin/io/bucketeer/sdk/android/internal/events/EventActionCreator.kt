package io.bucketeer.sdk.android.internal.events

import io.bucketeer.sdk.android.internal.dispatcher.Dispatcher
import io.bucketeer.sdk.android.internal.events.db.EventDao
import io.bucketeer.sdk.android.internal.events.dto.EventListDataChangedAction
import io.bucketeer.sdk.android.internal.model.Evaluation
import io.bucketeer.sdk.android.internal.model.Event
import io.bucketeer.sdk.android.internal.model.User
import io.bucketeer.sdk.android.internal.remote.ApiClient

internal class EventActionCreator(
  private val api: ApiClient,
  private val dispatcher: Dispatcher,
  private val eventDao: EventDao,
  private val featureTag: String
) {
  fun pushEvaluationEvent(
    timestamp: Long,
    evaluation: Evaluation,
    user: User
  ) {
    val event = generateEvaluationEvent(featureTag, timestamp, evaluation, user)
    eventDao.addEvent(event)
    val events = getAllEvent()
    dispatcher.send(EventListDataChangedAction(events))
  }

  fun pushDefaultEvaluationEvent(
    timestamp: Long,
    user: User,
    featureId: String
  ) {
    val event = generateDefaultEvaluationEvent(featureTag, timestamp, user, featureId)
    eventDao.addEvent(event)
    val events = getAllEvent()
    dispatcher.send(EventListDataChangedAction(events))
  }

  private fun getAllEvent(): List<Event> = eventDao.getEvents()

  fun pushGoalEvent(
    timestamp: Long,
    goalId: String,
    value: Double,
    user: User,
    evaluations: List<Evaluation>
  ) {
    val event = generateGoalEvent(featureTag, timestamp, goalId, value, user, evaluations)
    eventDao.addEvent(event)
    val events = getAllEvent()
    dispatcher.send(EventListDataChangedAction(events))
  }

  fun pushGetEvaluationLatencyMetricsEvent(
    latencyMills: Long,
    labels: Map<String, String>
  ) {
    val event = generateGetEvaluationLatencyMetricsEvent(latencyMills, labels)
    eventDao.addEvent(event)
    val events = getAllEvent()
    dispatcher.send(EventListDataChangedAction(events))
  }

  fun pushGetEvaluationSizeMetricsEvent(
    sizeByte: Int,
    labels: Map<String, String>
  ) {
    val event = generateGetEvaluationSizeMetricsEvent(sizeByte, labels)
    eventDao.addEvent(event)
    val events = getAllEvent()
    dispatcher.send(EventListDataChangedAction(events))
  }

  fun pushInternalErrorCountMetricsEvent(tag: String) {
    val event = generateInternalErrorCountMetricsEvent(tag)
    eventDao.addEvent(event)
    val events = getAllEvent()
    dispatcher.send(EventListDataChangedAction(events))
  }

  fun pushTimeoutErrorCountMetricsEvent(tag: String) {
    val event = generateTimeoutErrorCountMetricsEvent(tag)
    eventDao.addEvent(event)
    val events = getAllEvent()
    dispatcher.send(EventListDataChangedAction(events))
  }

  fun send(
    events: List<Event>,
    logSendingMaxBatchQueueCount: Int
  ) {
    if (events.isEmpty()) return
    val sendingEvents = events.take(logSendingMaxBatchQueueCount).toList()
//    val registerEvent = api.registerEvent(sendingEvents)
//    when (registerEvent) {
//      is Api.Result.Success -> {
//        val errorMap = registerEvent.value.errorsMap
//        val deleteIds = sendingEvents.map(EventOuterClass.Event::getId)
//          .filter {
//            // if the event does not contain in error, delete it
//            val error = errorMap[it] ?: return@filter true
//            // if the error is not retriable, delete it
//            !error.retriable
//          }
//
//        eventDao.delete(deleteIds)
//        dispatcher.send(EventListDataChangedAction(getAllEvent()))
//      }
//      is Api.Result.Fail -> {
//        logd(throwable = registerEvent.e) { "EventActionCreator: registerEvent Error" }
//      }
//    }
  }
}
