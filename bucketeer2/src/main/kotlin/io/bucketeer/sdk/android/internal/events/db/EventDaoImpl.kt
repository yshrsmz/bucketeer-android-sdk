package io.bucketeer.sdk.android.internal.events.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.squareup.moshi.Moshi
import io.bucketeer.sdk.android.internal.database.asSequence
import io.bucketeer.sdk.android.internal.database.getString
import io.bucketeer.sdk.android.internal.database.select
import io.bucketeer.sdk.android.internal.events.EventEntity.Companion.COLUMN_EVENT
import io.bucketeer.sdk.android.internal.events.EventEntity.Companion.COLUMN_ID
import io.bucketeer.sdk.android.internal.events.EventEntity.Companion.TABLE_NAME
import io.bucketeer.sdk.android.internal.model.Event

internal class EventDaoImpl(
  private val sqLiteOpenHelper: SupportSQLiteOpenHelper,
  moshi: Moshi
) : EventDao {

  private val eventAdapter = moshi.adapter(Event::class.java)

//  override fun addEvent(goalEvent: EventOuterClass.GoalEvent) {
//    addEvent(goalEvent.toEvent())
//  }
//
//  override fun addEvent(evaluationEvent: EventOuterClass.EvaluationEvent) {
//    addEvent(evaluationEvent.toEvent())
//  }
//
//  override fun addEvent(metricsEvent: EventOuterClass.MetricsEvent) {
//    addEvent(metricsEvent.toEvent())
//  }

  override fun addEvent(event: Event) {
    val contentValues = ContentValues().apply {
      put(COLUMN_ID, event.id)
      put(COLUMN_EVENT, eventAdapter.toJson(event))
    }

    sqLiteOpenHelper.writableDatabase.insert(
      TABLE_NAME,
      SQLiteDatabase.CONFLICT_REPLACE,
      contentValues
    )
  }

  override fun getEvents(): List<Event> {
    val c = sqLiteOpenHelper.readableDatabase.select(
      table = TABLE_NAME
    )

    return c.use {
      c.asSequence()
        .mapNotNull { eventAdapter.fromJson(it.getString(COLUMN_EVENT)) }
        .toList()
    }
  }

  override fun delete(ids: List<String>) {
    @Suppress("MoveLambdaOutsideParentheses")
    val valuesIn = List(ids.count(), { "?" }).joinToString(separator = ",")
    val whereArgs = ids.toTypedArray()

    sqLiteOpenHelper.writableDatabase.delete(
      TABLE_NAME,
      "$COLUMN_ID IN ($valuesIn)",
      whereArgs
    )
  }
}
