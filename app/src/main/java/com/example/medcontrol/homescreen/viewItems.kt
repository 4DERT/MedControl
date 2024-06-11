package com.example.medcontrol.homescreen

import androidx.compose.foundation.ScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
data class NotificationViewItem(
    val selectedDays: Map<DayOfWeek, Boolean>,
    val time: LocalTime,
    val isExpended: Boolean,
    val uuid: UUID,
    val timeState: TimePickerState,
    val scrollState: ScrollState
)

data class MedicineViewItem(
    val name: String,
    val notifications: List<NotificationViewItem>,
)

data class HomeScreenViewItem(
    val isAddMedicineModalVisible: Boolean,
)