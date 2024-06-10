package com.example.medcontrol.homescreen

sealed class ItemsListState {
    data class Success(val data: HomeScreenViewItem) : ItemsListState()
    data class Empty(val message: String) : ItemsListState()
    object Loading : ItemsListState()
}
