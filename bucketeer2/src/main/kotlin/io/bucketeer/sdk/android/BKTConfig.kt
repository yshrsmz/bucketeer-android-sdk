package io.bucketeer.sdk.android

import android.util.Patterns
import io.bucketeer.sdk.android.internal.logw

private const val DEFAULT_FLUSH_INTERVAL_MILLIS: Long = 30_000 // 30 seconds
private const val DEFAULT_MAX_QUEUE_SIZE: Int = 50
private const val DEFAULT_POLLING_INTERVAL_MILLIS: Long = 600_000 // 10 minutes
private const val DEFAULT_BACKGROUND_POLLING_INTERVAL_MILLIS: Long = 3_600_000 // 1 hour
private const val MIN_POLLING_INTERVAL_MILLIS: Long = 300_000 // 5 minutes
private const val MIN_BACKGROUND_POLLING_INTERVAL_MILLIS: Long = 1_200_000 // 20 minutes

data class BKTConfig(
  val apiKey: String,
  val endpoint: String,
  val featureTag: String,
  val eventsFlushInterval: Long,
  val eventsMaxBatchQueueCount: Int,
  val pollingInterval: Long,
  val backgroundPollingInterval: Long,
  val debugMode: Boolean
) {

  class Builder() {
    var apiKey: String? = null
    var endpoint: String? = null
    var featureTag: String? = null
    var eventsFlushInterval: Long = DEFAULT_FLUSH_INTERVAL_MILLIS
    var eventsMaxQueueSize: Int = DEFAULT_MAX_QUEUE_SIZE
    var pollingInterval: Long = DEFAULT_POLLING_INTERVAL_MILLIS
    var backgroundPollingInterval: Long = DEFAULT_BACKGROUND_POLLING_INTERVAL_MILLIS
    var debugMode: Boolean = false

    constructor(block: Builder.() -> Unit) : this() {
      this.apply(block)
    }

    fun apiKey(apiKey: String): Builder {
      this.apiKey = apiKey
      return this
    }

    fun endpoint(endpoint: String): Builder {
      this.endpoint = endpoint
      return this
    }

    fun featureTag(featureTag: String): Builder {
      this.featureTag = featureTag
      return this
    }

    fun eventsFlushInterval(interval: Long): Builder {
      this.eventsFlushInterval = interval
      return this
    }

    fun eventsMaxQueueSize(maxQueueSize: Int): Builder {
      this.eventsMaxQueueSize = maxQueueSize
      return this
    }

    fun pollingInterval(pollingInterval: Long): Builder {
      this.pollingInterval = pollingInterval
      return this
    }

    fun backgroundPollingInterval(backgroundPollingInterval: Long): Builder {
      this.backgroundPollingInterval = backgroundPollingInterval
      return this
    }

    fun debugMode(debugMode: Boolean): Builder {
      this.debugMode = debugMode
      return this
    }

    fun build(): BKTConfig {
      val apiKey = requireNotNull(this.apiKey) { "apiKey is required" }
      val endpoint = requireNotNull(this.endpoint) { "endpoint is required" }
      val featureTag = requireNotNull(this.featureTag) { "featureTag is required" }

      require(apiKey.isNotEmpty()) { "non-empty apiKey is required" }
      require(apiKey.isNotEmpty()) { "non-empty endpoint is required" }
      require(Patterns.WEB_URL.matcher(endpoint).matches()) { "endpoint is invalid" }
      require(featureTag.isNotEmpty()) { "featureTag is required" }

      if (pollingInterval < MIN_POLLING_INTERVAL_MILLIS) {
        logw {
          "pollingInterval: $pollingInterval was set below the minimum allowed: " +
            "$MIN_POLLING_INTERVAL_MILLIS. It will use the minimum value."
        }
        pollingInterval = MIN_POLLING_INTERVAL_MILLIS
      }

      if (backgroundPollingInterval < MIN_BACKGROUND_POLLING_INTERVAL_MILLIS) {
        logw {
          "backgroundPollingInterval: $backgroundPollingInterval was set below the minimum allowed: " +
            "$MIN_BACKGROUND_POLLING_INTERVAL_MILLIS. It will use the minimum value."
        }
        backgroundPollingInterval = MIN_BACKGROUND_POLLING_INTERVAL_MILLIS
      }

      return BKTConfig(
        apiKey = apiKey,
        endpoint = endpoint,
        featureTag = featureTag,
        eventsFlushInterval = this.eventsFlushInterval,
        eventsMaxBatchQueueCount = this.eventsMaxQueueSize,
        pollingInterval = this.pollingInterval,
        backgroundPollingInterval = this.backgroundPollingInterval,
        debugMode = this.debugMode
      )
    }
  }
}
