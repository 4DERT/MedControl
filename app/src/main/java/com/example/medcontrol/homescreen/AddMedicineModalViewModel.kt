package com.example.medcontrol.homescreen

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID


class AddMedicineModalViewModel : ViewModel() {

    val state = MutableStateFlow(AddMedicineViewItem(name = "", notifications = listOf()))

    @SuppressLint("StateFlowValueCalledInComposition")
    @OptIn(ExperimentalMaterial3Api::class)
    fun cardEvent(event: AddMedicineModalEvent) {
        when (event) {
            is AddMedicineModalEvent.SetName -> {
                state.update { it.copy(name = event.name) }
            }

            is AddMedicineModalEvent.ToggleNotification -> {
                state.update {
                    it.copy(notifications = it.notifications.map { notification ->
                        if (notification.uuid == event.uuid) {
                            notification.copy(isExpended = !notification.isExpended)
                        } else {
                            notification
                        }
                    }
                    )
                }
            }

            is AddMedicineModalEvent.AddNotification -> {
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
                    time = LocalTime.now(),
                    isExpended = true,
                    uuid = UUID.randomUUID(),
                    timeState = TimePickerState(0, 0, true),
                    scrollState = ScrollState(0)
                )

                state.update {
                    it.copy(notifications = updatedNotifications)
                }
            }

            is AddMedicineModalEvent.DeleteNotification -> {
                state.update { currentState ->
                    currentState.copy(
                        notifications = currentState.notifications.filter { it.uuid != event.uuid }
                    )
                }
            }

            is AddMedicineModalEvent.SetDay -> {
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
