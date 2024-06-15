package com.example.medcontrol.graphscreen

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medcontrol.graphdatabase.GraphDao
import com.example.medcontrol.graphdatabase.Pulse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime

class GraphScreenViewModel(
    graphDao: GraphDao,
    application: Application
): AndroidViewModel(application) {

    private val appContext: Context = getApplication<Application>().applicationContext

    val state = MutableStateFlow<GraphScreenState>(GraphScreenState.Loading)
    val fabState = MutableStateFlow(GraphScreenViewItem(false))

    init {
        state.value = GraphScreenState.Empty

//        val pulse = Pulse(
//            pulse = 66,
//            timestamp = 1
//        )

//        viewModelScope.launch {
//            graphDao.insert(pulse)
//        }



    }

    fun showModal() {
        fabState.update { it.copy(true) }
    }

    fun hideModal() {
        fabState.update { it.copy(false) }
    }


}