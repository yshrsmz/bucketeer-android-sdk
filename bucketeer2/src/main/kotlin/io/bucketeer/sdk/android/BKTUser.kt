package io.bucketeer.sdk.android

import io.bucketeer.sdk.android.internal.model.User

@Suppress("DataClassPrivateConstructor")
data class BKTUser private constructor(
  val id: String,
  var attributes: Map<String, String>
) {
  class Builder {
    private lateinit var id: String
    private var attributes: Map<String, String> = mutableMapOf()

    fun id(id: String): Builder {
      this.id = id
      return this
    }

    fun customAttributes(attributes: Map<String, String>): Builder {
      this.attributes = attributes
      return this
    }

    fun build(): BKTUser {
      if (id.isEmpty()) {
        throw BKTException.IllegalArgumentException(
          "The user id is required."
        )
      }
      return BKTUser(
        id,
        attributes
      )
    }
  }

  fun customAttributes(attributes: Map<String, String>) {
    this.attributes = attributes
  }

  companion object
}

internal fun BKTUser.toRequest(): User {
  return User(
    id = this.id,
    data = this.attributes
  )
}
