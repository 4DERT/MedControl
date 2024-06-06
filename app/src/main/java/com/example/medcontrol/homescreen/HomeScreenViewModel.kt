package com.example.medcontrol.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medcontrol.database.Medicine
import com.example.medcontrol.database.MedicineDao
import com.example.medcontrol.database.TakeDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val dao: MedicineDao,
) : ViewModel() {

    val state = MutableStateFlow<ItemsListState>(ItemsListState.Loading)

    init {
        // temporary
//        viewModelScope.launch {
//            val td1 = listOf<TakeDate> (
//                TakeDate(
//                    localTime = LocalTime.of(12, 0),
//                    isMonday = true,
//                    isTuesday = false,
//                    isWednesday = true,
//                    isThursday = true,
//                    isFriday = false,
//                    isSaturday = true,
//                    isSunday = true
//                )
//            )
//
//            val m1 = Medicine(
//                name = "Paracetamol",
//                id = null,
//                dates = td1
//            )
//
//            dao.insert(m1)
//        }

        viewModelScope.launch {
            val items = dao.getAll()

            if (items.isEmpty()) {
                state.value = ItemsListState.Empty("dupa")
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
                    nextTake = "TODO",
//                    icon = it.icon,
                    dates = makeDates(it.dates)
                )
            )
        }

        return list

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
                time = takeDate.localTime,
                dayList = dayList
            )
        }
    }


}