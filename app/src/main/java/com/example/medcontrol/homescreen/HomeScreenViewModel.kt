package com.example.medcontrol.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medcontrol.database.Medicine
import com.example.medcontrol.database.MedicineDao
import com.example.medcontrol.database.TakeDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HomeScreenViewModel(
    private val dao: MedicineDao,
) : ViewModel() {

    val state = MutableStateFlow<ItemsListState>(ItemsListState.Loading)

    init {
        viewModelScope.launch {
            val items = dao.getAll()

            if (items.isEmpty()) {
                state.value = ItemsListState.Empty("test")
                return@launch
            }

            state.value = ItemsListState.Success(makeList(items))
        }
    }

    private fun makeList(dbItems: List<Medicine>): List<MedicineViewItem> {
        val list = mutableListOf<MedicineViewItem>()

        dbItems.forEach {
            list.add(
                MedicineViewItem(
                    name = it.name,
                    nextTake = makeNextTake(it.dates),
//                    icon = it.icon,
                    dates = makeDates(it.dates)
                )
            )
        }

        return list

    }

    private fun makeNextTake(dates: List<TakeDate>): String {
        val now = LocalDateTime.now()
        val currentDayOfWeek = now.dayOfWeek
        val currentTime = now.toLocalTime()

        // Lista dni tygodnia w kolejności od dzisiejszego
        val daysOfWeek = DayOfWeek.values().drop(currentDayOfWeek.ordinal) + DayOfWeek.values().take(currentDayOfWeek.ordinal)

        var nextDateTime: LocalDateTime? = null

        // Szukamy najbliższego terminu
        for (day in daysOfWeek) {
            val matchingDates = dates.filter { takeDate ->
                when (day) {
                    DayOfWeek.MONDAY -> takeDate.isMonday
                    DayOfWeek.TUESDAY -> takeDate.isTuesday
                    DayOfWeek.WEDNESDAY -> takeDate.isWednesday
                    DayOfWeek.THURSDAY -> takeDate.isThursday
                    DayOfWeek.FRIDAY -> takeDate.isFriday
                    DayOfWeek.SATURDAY -> takeDate.isSaturday
                    DayOfWeek.SUNDAY -> takeDate.isSunday
                    else -> false
                }
            }

            for (takeDate in matchingDates) {
                val candidateTime = LocalTime.of(takeDate.hour, takeDate.minute)
                val candidateDate = if (day == currentDayOfWeek && candidateTime.isAfter(currentTime)) {
                    now.withHour(takeDate.hour).withMinute(takeDate.minute).withSecond(0).withNano(0)
                } else {
                    val daysToAdd = (day.value - currentDayOfWeek.value + 7) % 7
                    now.plusDays(daysToAdd.toLong()).withHour(takeDate.hour).withMinute(takeDate.minute).withSecond(0).withNano(0)
                }

                if (nextDateTime == null || candidateDate.isBefore(nextDateTime)) {
                    nextDateTime = candidateDate
                }
            }

            if (nextDateTime != null) break
        }

        return nextDateTime?.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            ?: "No upcoming dates found"
    }

    private fun makeDates(dates: List<TakeDate>): List<TakeDateViewItem> {
        return dates.map { takeDate ->
            val dayList = listOf(
                "Monday" to takeDate.isMonday,
                "Tuesday" to takeDate.isTuesday,
                "Wednesday" to takeDate.isWednesday,
                "Thursday" to takeDate.isThursday,
                "Friday" to takeDate.isFriday,
                "Saturday" to takeDate.isSaturday,
                "Sunday" to takeDate.isSunday
            )
            TakeDateViewItem(
                hour = takeDate.hour,
                minute = takeDate.minute,
                dayList = dayList
            )
        }
    }

    fun showAddMedicineModal() {

    }

}