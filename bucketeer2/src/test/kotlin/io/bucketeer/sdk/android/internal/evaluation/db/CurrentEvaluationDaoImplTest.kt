package io.bucketeer.sdk.android.internal.evaluation.db

import android.database.Cursor
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import io.bucketeer.sdk.android.internal.database.createDatabase
import io.bucketeer.sdk.android.internal.database.getString
import io.bucketeer.sdk.android.internal.database.select
import io.bucketeer.sdk.android.internal.di.DataModule
import io.bucketeer.sdk.android.internal.model.Evaluation
import io.bucketeer.sdk.android.mocks.evaluation1
import io.bucketeer.sdk.android.mocks.user1Evaluations
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CurrentEvaluationDaoImplTest {

  private lateinit var dao: CurrentEvaluationDaoImpl
  private lateinit var openHelper: SupportSQLiteOpenHelper
  private lateinit var moshi: Moshi

  @Before
  fun setup() {
    moshi = DataModule.createMoshi()
    openHelper = createDatabase(ApplicationProvider.getApplicationContext())

    dao = CurrentEvaluationDaoImpl(openHelper, moshi)
  }

  @After
  fun tearDown() {
    openHelper.close()
  }

  @Test
  fun `upsertEvaluation - insert`() {
    dao.upsertEvaluation(user1Evaluations.evaluations[0])

    val c = getEvaluations()

    c.use {
      it.moveToFirst()

      val userId = it.getString(CurrentEvaluationEntity.COLUMN_USER_ID)
      assertThat(userId).isEqualTo("user id 1")

      val jsonStr = it.getString(CurrentEvaluationEntity.COLUMN_EVALUATION)
      val evaluation = moshi.adapter(Evaluation::class.java).fromJson(jsonStr)
      assertThat(evaluation).isEqualTo(evaluation1)

      assertThat(it.moveToNext()).isFalse()
    }
  }

  @Test
  fun `upsertEvaluation - update`() {
    val sourceEvaluation = user1Evaluations.evaluations[0]
    val variationValue = "updated value"
    val updatedEvaluation = sourceEvaluation.copy(
      variation_value = variationValue,
      variation = sourceEvaluation.variation.copy(value = variationValue)
    )

    dao.upsertEvaluation(sourceEvaluation)

    val beforeCursor = getEvaluations()
    beforeCursor.use {
      it.moveToFirst()

      val userId = it.getString(CurrentEvaluationEntity.COLUMN_USER_ID)
      assertThat(userId).isEqualTo("user id 1")

      val jsonStr = it.getString(CurrentEvaluationEntity.COLUMN_EVALUATION)
      val evaluation = moshi.adapter(Evaluation::class.java).fromJson(jsonStr)
      assertThat(evaluation).isEqualTo(sourceEvaluation)

      assertThat(it.moveToNext()).isFalse()
    }

    dao.upsertEvaluation(updatedEvaluation)

    val afterCursor = getEvaluations()
    afterCursor.use {
      it.moveToFirst()

      val userId = it.getString(CurrentEvaluationEntity.COLUMN_USER_ID)
      assertThat(userId).isEqualTo("user id 1")

      val jsonStr = it.getString(CurrentEvaluationEntity.COLUMN_EVALUATION)
      val evaluation = moshi.adapter(Evaluation::class.java).fromJson(jsonStr)
      assertThat(evaluation).isEqualTo(updatedEvaluation)

      assertThat(it.moveToNext()).isFalse()
    }
  }

  @Test
  fun `deleteNotIn - delete all`() {
    
  }

  private fun getEvaluations(): Cursor {
    val columns = arrayOf(
      CurrentEvaluationEntity.COLUMN_USER_ID,
      CurrentEvaluationEntity.COLUMN_EVALUATION
    )
    return openHelper.readableDatabase.select(
      table = CurrentEvaluationEntity.TABLE_NAME,
      columns = columns
    )
  }
}

