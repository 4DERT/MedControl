package com.example.medcontrol.homescreen.modal

import java.time.DayOfWeek
import java.util.UUID

sealed class AddMedicineModalEvent {
    data class SetName(val name: String): AddMedicineModalEvent()
    data object AddNotification: AddMedicineModalEvent()
    data class ToggleNotification(val uuid: UUID ): AddMedicineModalEvent()
    data class DeleteNotification(val uuid: UUID ): AddMedicineModalEvent()
    data class SetDay(val uuid: UUID, val day: DayOfWeek, val isChecked: Boolean): AddMedicineModalEvent()
}