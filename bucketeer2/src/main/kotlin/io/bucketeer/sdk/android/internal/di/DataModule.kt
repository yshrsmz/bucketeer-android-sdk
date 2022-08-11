package io.bucketeer.sdk.android.internal.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.squareup.moshi.Moshi
import io.bucketeer.sdk.android.internal.Constants
import io.bucketeer.sdk.android.internal.database.createDatabase
import io.bucketeer.sdk.android.internal.model.jsonadapter.EventAdapterFactory
import io.bucketeer.sdk.android.internal.model.jsonadapter.EventTypeAdapter
import io.bucketeer.sdk.android.internal.model.jsonadapter.MetricsEventAdapterFactory
import io.bucketeer.sdk.android.internal.model.jsonadapter.MetricsEventTypeAdapter
import io.bucketeer.sdk.android.internal.model.jsonadapter.ReasonTypeAdapter
import io.bucketeer.sdk.android.internal.model.jsonadapter.SourceIDAdapter
import io.bucketeer.sdk.android.internal.model.jsonadapter.UserEvaluationsStateAdapter
import io.bucketeer.sdk.android.internal.remote.ApiClient
import io.bucketeer.sdk.android.internal.remote.ApiClientImpl

internal class DataModule(
  application: Application,
  apiKey: String,
  endpoint: String,
  featureTag: String
) {

  private val moshi: Moshi by lazy { moshi() }

  @VisibleForTesting
  internal val api: ApiClient = ApiClientImpl(endpoint, apiKey, featureTag, moshi)

  private val sqliteOpenHelper: SupportSQLiteOpenHelper by lazy {
    createDatabase(application)
  }

  internal val sharedPreferences: SharedPreferences by lazy {
    application.getSharedPreferences(Constants.PREFERENCES_NAME, Context.MODE_PRIVATE)
  }

  companion object {
    @VisibleForTesting
    internal fun moshi(): Moshi {
      return Moshi.Builder()
        .add(EventTypeAdapter())
        .add(MetricsEventTypeAdapter())
        .add(ReasonTypeAdapter())
        .add(SourceIDAdapter())
        .add(EventAdapterFactory())
        .add(MetricsEventAdapterFactory())
        .add(UserEvaluationsStateAdapter())
        .build()
    }
  }
}
