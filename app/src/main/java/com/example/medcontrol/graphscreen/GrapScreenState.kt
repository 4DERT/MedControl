package com.example.medcontrol.graphscreen


sealed class GraphScreenState {
    data class Success(val data: GraphDataViewItem) : GraphScreenState()
    object Empty : GraphScreenState()
    object Loading : GraphScreenState()
}