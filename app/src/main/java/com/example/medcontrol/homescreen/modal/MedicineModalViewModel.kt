package com.example.medcontrol.homescreen.modal

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.lifecycle.ViewModel
import com.example.medcontrol.homescreen.MedicineViewItem
import com.example.medcontrol.homescreen.NotificationViewItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.util.UUID


class MedicineModalViewModel(data: MedicineViewItem? = null) : ViewModel() {

    val state = if (data == null) {
        MutableStateFlow(MedicineViewItem(id = 0, name = "", notifications = listOf()))
    } else {
        MutableStateFlow(data)
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    fun cardEvent(event: MedicineModalEvent) {
        when (event) {
            is MedicineModalEvent.SetName -> {
                state.update { it.copy(name = event.name) }
            }

            is MedicineModalEvent.ToggleNotification -> {
                state.update {
                    it.copy(notifications = it.notifications.map { notification ->
                        if (notification.uuid == event.uuid) {
                            notification.copy(isExpended = !notification.isExpended)
                        } else {
                            notification.copy(isExpended = false)
                        }
                    }
                    )
                }
            }

            is MedicineModalEvent.Notification -> {
                val uuid = UUID.randomUUID()
                val updatedNotifications = state.value.notifications + NotificationViewItem(
                    selectedDays = mapOf(
                        DayOfWeek.MONDAY to false,
                        DayOfWeek.TUESDAY to false,
                        DayOfWeek.WEDNESDAY to false,
                        DayOfWeek.THURSDAY to false,
                        DayOfWeek.FRIDAY to false,
                        DayOfWeek.SATURDAY to false,
                        DayOfWeek.SUNDAY to false,
                    ),
                    isExpended = false,
                    uuid = uuid,
                    timeState = TimePickerState(0, 0, true),
                    scrollState = ScrollState(0)
                )

                state.update {
                    it.copy(notifications = updatedNotifications)
                }

                // hide all notifications
                cardEvent(MedicineModalEvent.ToggleNotification(uuid))
            }

            is MedicineModalEvent.DeleteNotification -> {
                state.update { currentState ->
                    currentState.copy(
                        notifications = currentState.notifications.filter { it.uuid != event.uuid }
                    )
                }
            }

            is MedicineModalEvent.SetDay -> {
                state.update {
                    it.copy(notifications = it.notifications.map { notification ->
                        if (notification.uuid == event.uuid) {
                            notification.copy(
                                selectedDays = notification.selectedDays.toMutableMap().apply {
                                    this[event.day] = event.isChecked
                                })
                        } else {
                            notification
                        }
                    })
                }
            }
        }
    }


}
