package io.bucketeer.sdk.android.internal.events

import io.bucketeer.sdk.android.internal.dispatcher.Dispatcher
import io.bucketeer.sdk.android.internal.events.dto.EventListDataChangedAction
import io.bucketeer.sdk.android.internal.model.Event
import io.bucketeer.sdk.android.internal.util.ObservableField
import java.util.Observable

internal class EventStore(
  private val dispatcher: Dispatcher
) {
  val events: ObservableField<List<Event>> = ObservableField(listOf())

  init {
    dispatcher.addObserver { _: Observable?, arg: Any? ->
      when (arg) {
        is EventListDataChangedAction ->
          // Because it loops endlessly
          // when event is exactly 50 and retriable = true is returned from the server
          if (events.value != arg.events) {
            events.value = arg.events
          }
      }
    }
  }
}
