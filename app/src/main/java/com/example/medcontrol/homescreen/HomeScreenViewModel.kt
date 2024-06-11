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
import java.time.LocalTime


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

    @OptIn(ExperimentalMaterial3Api::class)
    private fun makeList(dbItems: List<MedicineEntity>): List<MedicineViewItem> {
        return dbItems.map { medicineEntity ->
            MedicineViewItem(
                id = medicineEntity.id,
                name = medicineEntity.name,
                notifications = medicineEntity.notifications.map { notificationEntity ->
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
            )
        }
    }

//    private fun makeNextTake(dates: List<TakeDate>): String {
//        val now = LocalDateTime.now()
//        val currentDayOfWeek = now.dayOfWeek
//        val currentTime = now.toLocalTime()
//
//        // Lista dni tygodnia w kolejności od dzisiejszego
//        val daysOfWeek = DayOfWeek.values().drop(currentDayOfWeek.ordinal) + DayOfWeek.values().take(currentDayOfWeek.ordinal)
//
//        var nextDateTime: LocalDateTime? = null
//
//        // Szukamy najbliższego terminu
//        for (day in daysOfWeek) {
//            val matchingDates = dates.filter { takeDate ->
//                when (day) {
//                    DayOfWeek.MONDAY -> takeDate.isMonday
//                    DayOfWeek.TUESDAY -> takeDate.isTuesday
//                    DayOfWeek.WEDNESDAY -> takeDate.isWednesday
//                    DayOfWeek.THURSDAY -> takeDate.isThursday
//                    DayOfWeek.FRIDAY -> takeDate.isFriday
//                    DayOfWeek.SATURDAY -> takeDate.isSaturday
//                    DayOfWeek.SUNDAY -> takeDate.isSunday
//                    else -> false
//                }
//            }
//
//            for (takeDate in matchingDates) {
//                val candidateTime = LocalTime.of(takeDate.hour, takeDate.minute)
//                val candidateDate = if (day == currentDayOfWeek && candidateTime.isAfter(currentTime)) {
//                    now.withHour(takeDate.hour).withMinute(takeDate.minute).withSecond(0).withNano(0)
//                } else {
//                    val daysToAdd = (day.value - currentDayOfWeek.value + 7) % 7
//                    now.plusDays(daysToAdd.toLong()).withHour(takeDate.hour).withMinute(takeDate.minute).withSecond(0).withNano(0)
//                }
//
//                if (nextDateTime == null || candidateDate.isBefore(nextDateTime)) {
//                    nextDateTime = candidateDate
//                }
//            }
//
//            if (nextDateTime != null) break
//        }
//
//        return nextDateTime?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
//            ?: "No upcoming dates found"
//    }
//
//    private fun makeDates(dates: List<TakeDate>): List<TakeDateViewItem> {
//        return dates.map { takeDate ->
//            val dayList = listOf(
//                "Monday" to takeDate.isMonday,
//                "Tuesday" to takeDate.isTuesday,
//                "Wednesday" to takeDate.isWednesday,
//                "Thursday" to takeDate.isThursday,
//                "Friday" to takeDate.isFriday,
//                "Saturday" to takeDate.isSaturday,
//                "Sunday" to takeDate.isSunday
//            )
//            TakeDateViewItem(
//                hour = takeDate.hour,
//                minute = takeDate.minute,
//                dayList = dayList
//            )
//        }
//    }

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