package io.bucketeer.sdk.android.internal.scheduler

import io.bucketeer.sdk.android.BKTConfig
import io.bucketeer.sdk.android.internal.di.Component
import io.bucketeer.sdk.android.internal.event.EventInteractor
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * use ExecutorService
 */
internal class EventForegroundScheduler(
  private val config: BKTConfig,
  private val component: Component,
  private val executor: ScheduledExecutorService
) {

  private var scheduledFuture: ScheduledFuture<*>? = null

  private val eventUpdateListener = EventInteractor.EventUpdateListener { events ->
    executor.submit { sendEvents() }
    reschedule()
  }

  fun reschedule() {
    scheduledFuture?.cancel(false)
    scheduledFuture = executor.scheduleWithFixedDelay(
      {},
      config.pollingInterval,
      config.pollingInterval,
      TimeUnit.MILLISECONDS
    )
  }

  fun start() {
    component.eventInteractor.setEventUpdateListener(this.eventUpdateListener)
    reschedule()
  }

  fun stop() {
    component.eventInteractor.setEventUpdateListener(null)
    scheduledFuture?.cancel(false)
  }

  private fun sendEvents() {
    component.eventInteractor.sendEvents(force = true)
  }
}
