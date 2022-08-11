package io.bucketeer.sdk.android.internal.evaluation.dto

import io.bucketeer.sdk.android.BKTException

internal data class RefreshManuallyStateChangedAction(
  val state: State
) {
  sealed class State {
    object Loading : State()
    object Loaded : State()
    class Error(val exception: BKTException) : State()
  }
}
