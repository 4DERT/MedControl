package com.example.medcontrol.homescreen

import com.example.medcontrol.homescreen.modal.AddMedicineViewItem

sealed class ItemsListState {
    data class Success(val data: List<AddMedicineViewItem>) : ItemsListState()
    data class Empty(val message: String) : ItemsListState()
    object Loading : ItemsListState()
}
