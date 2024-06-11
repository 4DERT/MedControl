package com.example.medcontrol.homescreen.modal

import java.time.DayOfWeek
import java.util.UUID

sealed class MedicineModalEvent {
    data class SetName(val name: String): MedicineModalEvent()
    data object Notification: MedicineModalEvent()
    data class ToggleNotification(val uuid: UUID ): MedicineModalEvent()
    data class DeleteNotification(val uuid: UUID ): MedicineModalEvent()
    data class SetDay(val uuid: UUID, val day: DayOfWeek, val isChecked: Boolean): MedicineModalEvent()
}