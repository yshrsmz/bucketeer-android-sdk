package io.bucketeer.sdk.android.internal.event

import io.bucketeer.sdk.android.internal.Clock
import io.bucketeer.sdk.android.internal.IdGenerator
import io.bucketeer.sdk.android.internal.event.db.EventDao
import io.bucketeer.sdk.android.internal.logd
import io.bucketeer.sdk.android.internal.model.Evaluation
import io.bucketeer.sdk.android.internal.model.Event
import io.bucketeer.sdk.android.internal.model.User
import io.bucketeer.sdk.android.internal.model.UserEvaluationsState
import io.bucketeer.sdk.android.internal.remote.ApiClient
import io.bucketeer.sdk.android.internal.remote.RegisterEventsResult
import io.bucketeer.sdk.android.internal.util.ObservableField
import java.io.InterruptedIOException
import java.net.SocketTimeoutException

internal class EventInteractor(
  private val eventsMaxBatchQueueCount: Int,
  private val apiClient: ApiClient,
  private val eventDao: EventDao,
  private val clock: Clock,
  private val idGenerator: IdGenerator,
) {
  val events: ObservableField<List<Event>> = ObservableField(emptyList())

  fun trackEvaluationEvent(featureTag: String, user: User, evaluation: Evaluation) {
    eventDao.addEvent(
      newEvaluationEvent(clock, idGenerator, featureTag, user, evaluation)
    )

    events.value = eventDao.getEvents()
  }

  fun trackDefaultEvaluationEvent(featureTag: String, user: User, featureId: String) {
    eventDao.addEvent(
      newDefaultEvaluationEvent(clock, idGenerator, featureTag, user, featureId)
    )

    events.value = eventDao.getEvents()
  }

  fun trackGoalEvent(featureTag: String, user: User, goalId: String, value: Double) {
    eventDao.addEvent(
      newGoalEvent(clock, idGenerator, goalId, value, featureTag, user)
    )

    events.value = eventDao.getEvents()
  }

  fun trackFetchEvaluationsSuccess(
    featureTag: String,
    mills: Long,
    sizeByte: Int,
    state: UserEvaluationsState
  ) {
    eventDao.addEvents(
      listOf(
        newGetEvaluationLatencyMetricsEvent(clock, idGenerator, mills, featureTag, state),
        newGetEvaluationSizeMetricsEvent(clock, idGenerator, sizeByte, featureTag, state)
      )
    )

    events.value = eventDao.getEvents()
  }

  fun trackFetchEvaluationsFailure(
    featureTag: String,
    error: Throwable
  ) {
    // TODO: BKTException
    val event = when (error) {
      is SocketTimeoutException,
      is InterruptedIOException -> newTimeoutErrorCountMetricsEvent(clock, idGenerator, featureTag)
      else -> newInternalErrorCountMetricsEvent(clock, idGenerator, featureTag)
    }

    eventDao.addEvent(event)

    events.value = eventDao.getEvents()
  }

  fun sendEvents(force: Boolean = false) {
    val current = events.value

    if (current.isEmpty()) return
    if (!force && current.size < eventsMaxBatchQueueCount) return

    val sendingEvents = current.take(eventsMaxBatchQueueCount)

    @Suppress("MoveVariableDeclarationIntoWhen")
    val result = apiClient.registerEvents(sendingEvents)

    when (result) {
      is RegisterEventsResult.Success -> {
        val errors = result.value.data.errors
        val deleteIds = sendingEvents.map { it.id }
          .filter { eventId ->
            // if the event does not contain in error, delete it
            val error = errors[eventId] ?: return@filter true
            // if the error is not retriable, delete it
            !error.retriable
          }

        eventDao.delete(deleteIds)

        events.value = eventDao.getEvents()
      }
      is RegisterEventsResult.Failure -> {
        logd(throwable = result.error) { "Failed to register events" }
      }
    }

  }
}
