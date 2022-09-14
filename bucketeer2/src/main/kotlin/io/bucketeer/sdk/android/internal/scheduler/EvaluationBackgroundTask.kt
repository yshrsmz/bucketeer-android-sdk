package io.bucketeer.sdk.android.internal.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import io.bucketeer.sdk.android.BKTClient
import io.bucketeer.sdk.android.BKTClientImpl
import io.bucketeer.sdk.android.internal.logd
import io.bucketeer.sdk.android.internal.loge
import io.bucketeer.sdk.android.internal.logi

internal class EvaluationBackgroundTask : BroadcastReceiver() {
  override fun onReceive(context: Context?, intent: Intent?) {
    // As we can't serialize logger we need to use BKTClient singleton.
    // This means
    // - BKTClient#initialize must be called first
    // - background polling happens as long as BKTClient singleton lives
    val client = try {
      BKTClient.getInstance() as BKTClientImpl
    } catch (e: Throwable) {
      null
    }

    if (client == null) {
      logd { "BKTClient is not initialized, skipping background polling..." }
      return
    }
    val pendingResult = goAsync()

    client.executor.execute {
      val result = BKTClientImpl.fetchEvaluationsSync(client.component, client.executor, null)

      if (result == null) {
        logd { "finished background polling" }
      } else {
        loge(result) { "background polling finished with error" }
      }

      pendingResult.finish()
    }
  }

  companion object {
    private fun createAlarmIntent(context: Context): Intent {
      return Intent(context, EvaluationBackgroundTask::class.java)
    }

    private fun createPendingIntent(context: Context): PendingIntent {
      return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        PendingIntent.getBroadcast(
          context,
          0,
          createAlarmIntent(context),
          PendingIntent.FLAG_IMMUTABLE,
        )
      } else {
        PendingIntent.getBroadcast(context, 0, createAlarmIntent(context), 0)
      }
    }

    private fun getAlarmManager(context: Context): AlarmManager {
      return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    fun start(context: Context, interval: Long) {
      stop(context)
      logi { "start Evaluation background polling...: $interval" }

      val intent = createPendingIntent(context)
      val alarmManager = getAlarmManager(context)

      try {
        alarmManager.setInexactRepeating(
          AlarmManager.ELAPSED_REALTIME,
          SystemClock.elapsedRealtime() + interval,
          interval,
          intent,
        )
      } catch (e: Throwable) {
        loge(e) { "Error while starting Evaluation background polling." }
      }
    }

    fun stop(context: Context) {
      logi { "stop Evaluation background polling..." }
      val alarmManager = getAlarmManager(context)
      val intent = createPendingIntent(context)
      alarmManager.cancel(intent)
    }
  }
}
