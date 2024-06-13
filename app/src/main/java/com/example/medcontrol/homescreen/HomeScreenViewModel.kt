package com.example.medcontrol.homescreen

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medcontrol.database.MedicineDao
import com.example.medcontrol.database.MedicineEntity
import com.example.medcontrol.database.NotificationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters


class HomeScreenViewModel(
    private val dao: MedicineDao,
) : ViewModel() {

    val state = MutableStateFlow<ItemsListState>(ItemsListState.Loading)
    val fabState = MutableStateFlow(HomeScreenViewItem(isAddMedicineModalVisible = false))

    private val medicineList: StateFlow<List<MedicineEntity>> = dao.getAll()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    init {
        viewModelScope.launch {
            medicineList.collect { items ->
                if (items.isEmpty()) {
                    state.value = ItemsListState.Empty("test")
                } else {
                    state.value = ItemsListState.Success(makeList(items))
                }
            }
        }
    }

    private fun makeList(dbItems: List<MedicineEntity>): List<MedicineViewItem> {
        return dbItems.map { medicineEntity ->
            MedicineViewItem(
                id = medicineEntity.id,
                name = medicineEntity.name,
                notifications = makeNotificationViewItems(medicineEntity),
                nextTake = makeNextTakeString( makeNotificationViewItems(medicineEntity) ),
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun makeNotificationViewItems(medicineEntity: MedicineEntity) =
        medicineEntity.notifications.map { notificationEntity ->
            NotificationViewItem(
                selectedDays = notificationEntity.selectedDays,
                isExpended = false,
                uuid = notificationEntity.uuid,
                timeState = TimePickerState(
                    notificationEntity.hour,
                    notificationEntity.minute,
                    true
                ),
                scrollState = ScrollState(0)
            )
        }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun makeNextTakeString(notifications: List<NotificationViewItem>): String? {
        val now = LocalDateTime.now()
        val today = now.dayOfWeek
        val currentTime = now.toLocalTime()

        val nextNotification = notifications
            .flatMap { notification ->
                notification.selectedDays
                    .filter { it.value }
                    .map { day ->
                        val notificationTime = LocalTime.of(notification.timeState.hour, notification.timeState.minute)
                        val dayDifference = (day.key.value - today.value + 7) % 7
                        val notificationDateTime = if (dayDifference == 0 && notificationTime.isAfter(currentTime)) {
                            now.withHour(notification.timeState.hour).withMinute(notification.timeState.minute)
                        } else {
                            now.plusDays((if (dayDifference == 0) 7 else dayDifference).toLong())
                                .withHour(notification.timeState.hour)
                                .withMinute(notification.timeState.minute)
                        }
                        notificationDateTime
                    }
            }
            .minOrNull()

        return nextNotification?.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
    }


    fun showAddMedicineModal() {
        fabState.update { it.copy(isAddMedicineModalVisible = true, medicineToEdit = null) }
    }

    fun dismissAddMedicineModal() {
        hideAddMedicineModal()
    }

    fun hideAddMedicineModal() {
        fabState.update { it.copy(isAddMedicineModalVisible = false) }
    }

    fun addMedicine(medicine: MedicineViewItem) {
        viewModelScope.launch {
            val medicineEntity = toMedicineEntity(medicine)
            dao.insertMedicine(medicineEntity)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun toMedicineEntity(viewItem: MedicineViewItem): MedicineEntity {
        val entity =  MedicineEntity(
            id = viewItem.id,
            name = viewItem.name,
            notifications = viewItem.notifications.map { notificationViewItem ->
                NotificationEntity(
                    selectedDays = notificationViewItem.selectedDays,
                    hour = notificationViewItem.timeState.hour,
                    minute = notificationViewItem.timeState.minute,
                    uuid = notificationViewItem.uuid
                )
            }
        )

        return entity
    }

    fun showMedicineDetails(item: MedicineViewItem) {
        fabState.update { it.copy(isAddMedicineModalVisible = true, medicineToEdit = item) }
    }

    fun updateMedicine(item: MedicineViewItem) {
        viewModelScope.launch {
            dao.updateMedicine(toMedicineEntity(item))
        }
    }

}