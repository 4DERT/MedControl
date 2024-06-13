package com.example.medcontrol.homescreen

import androidx.compose.foundation.ScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import java.time.DayOfWeek
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
data class NotificationViewItem(
    val selectedDays: Map<DayOfWeek, Boolean>,
    val isExpended: Boolean,
    val uuid: UUID,
    val timeState: TimePickerState,
    val scrollState: ScrollState
)

data class MedicineViewItem(
    val id: Long,
    val name: String,
    val notifications: List<NotificationViewItem>,
    val nextTake: String? = null
)

data class HomeScreenViewItem(
    val isAddMedicineModalVisible: Boolean,
    val medicineToEdit: MedicineViewItem? = null,
)