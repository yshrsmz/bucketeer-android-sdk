package io.bucketeer.sdk.android.internal.evaluation.db

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.squareup.moshi.Moshi
import io.bucketeer.sdk.android.internal.database.asSequence
import io.bucketeer.sdk.android.internal.database.getString
import io.bucketeer.sdk.android.internal.database.select
import io.bucketeer.sdk.android.internal.database.transaction
import io.bucketeer.sdk.android.internal.evaluation.db.LatestEvaluationEntity.Companion.COLUMN_EVALUATION
import io.bucketeer.sdk.android.internal.evaluation.db.LatestEvaluationEntity.Companion.COLUMN_FEATURE_ID
import io.bucketeer.sdk.android.internal.evaluation.db.LatestEvaluationEntity.Companion.COLUMN_USER_ID
import io.bucketeer.sdk.android.internal.evaluation.db.LatestEvaluationEntity.Companion.TABLE_NAME
import io.bucketeer.sdk.android.internal.model.Evaluation
import io.bucketeer.sdk.android.internal.model.User

internal class LatestEvaluationDaoImpl(
  private val sqLiteOpenHelper: SupportSQLiteOpenHelper,
  moshi: Moshi
) : LatestEvaluationDao {

  private val adapter = moshi.adapter(Evaluation::class.java)

  override fun put(user: User, list: List<Evaluation>) {
    sqLiteOpenHelper.writableDatabase.transaction {
      list.forEach { evaluation ->
        val affectedRow = update(this@transaction, user, evaluation)
        if (affectedRow == 0) {
          insert(this@transaction, user, evaluation)
        }
      }
    }
  }

  private fun insert(
    database: SupportSQLiteDatabase,
    user: User,
    evaluation: Evaluation
  ): Long {
    val contentValue = ContentValues().apply {
      put(COLUMN_USER_ID, user.id)
      put(COLUMN_FEATURE_ID, evaluation.feature_id)
      put(COLUMN_EVALUATION, adapter.toJson(evaluation))
    }
    return database.insert(TABLE_NAME, SQLiteDatabase.CONFLICT_REPLACE, contentValue)
  }

  private fun update(
    database: SupportSQLiteDatabase,
    user: User,
    evaluation: Evaluation
  ): Int {
    val contentValues = ContentValues().apply {
      put(COLUMN_EVALUATION, adapter.toJson(evaluation))
    }
    return database.update(
      TABLE_NAME,
      SQLiteDatabase.CONFLICT_REPLACE,
      contentValues,
      "$COLUMN_USER_ID=? AND $COLUMN_FEATURE_ID=?",
      arrayOf(user.id, evaluation.feature_id)
    )
  }

  override fun get(user: User): List<Evaluation> {
    val projection = arrayOf(COLUMN_USER_ID, COLUMN_EVALUATION)
    val c = sqLiteOpenHelper.readableDatabase.select(
      table = TABLE_NAME,
      columns = projection,
      selection = "$COLUMN_USER_ID=?",
      selectionArgs = arrayOf(user.id)
    )

    return c.use {
      c.asSequence()
        .mapNotNull { adapter.fromJson(it.getString(COLUMN_EVALUATION)) }
        .toList()
    }
  }

  private fun deleteAll(
    database: SupportSQLiteDatabase,
    user: User
  ) {
    database.delete(
      TABLE_NAME,
      "$COLUMN_USER_ID=?",
      arrayOf(user.id)
    )
  }

  override fun deleteAllAndInsert(
    user: User,
    list: List<Evaluation>
  ): Boolean {
    sqLiteOpenHelper.writableDatabase.transaction {
      deleteAll(this, user)
      list.forEach {
        if (insert(this, user, it) == -1L) {
          return false
        }
      }
    }
    return true
  }
}
