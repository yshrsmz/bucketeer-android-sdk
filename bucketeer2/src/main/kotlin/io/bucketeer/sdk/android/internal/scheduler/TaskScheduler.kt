package io.bucketeer.sdk.android.internal.scheduler

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import io.bucketeer.sdk.android.internal.di.Component
import java.util.concurrent.ScheduledExecutorService

internal class TaskScheduler(
  private val component: Component,
  executor: ScheduledExecutorService,
) : DefaultLifecycleObserver {

  private val foregroundSchedulers: List<ScheduledTask> = listOf(
    EvaluationForegroundTask(component, executor),
    EventForegroundTask(component, executor),
  )

  private val backgroundSchedulers: List<ScheduledTask> = listOf(
    EvaluationBackgroundTask.Scheduler(
      component.context,
      component.config.backgroundPollingInterval,
    ),
    EventBackgroundTask.Scheduler(
      component.context,
      component.config.pollingInterval,
    ),
  )

  // app start or back to foreground
  override fun onStart(owner: LifecycleOwner) {
    // start foreground tasks
    foregroundSchedulers.forEach { it.start() }

    // stop background task
    backgroundSchedulers.forEach { it.stop() }
  }

  // to background
  override fun onStop(owner: LifecycleOwner) {
    // stop foreground tasks
    foregroundSchedulers.forEach { it.stop() }

    // start background task
    backgroundSchedulers.forEach { it.start() }
  }

  fun stop() {
    foregroundSchedulers.forEach { it.stop() }
    backgroundSchedulers.forEach { it.stop() }
  }
}
