package com.example.medcontrol.graphscreen.modal

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.medcontrol.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class GraphModalViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val appContext: Context = getApplication<Application>().applicationContext

    val state = MutableStateFlow<GraphModalState>(
        GraphModalState(
            options = listOf(Option.HEART_RATE, Option.BLOOD_SUGAR, Option.BLOOD_PRESSURE),
            selectedOption = null,
            timestamp = null,
            pulse = null,
            systolic = null,
            diastolic = null,
            bloodSugar = null
        )
    )

    fun modalEvent(event: GraphModalEvent) {
        when (event) {
            is GraphModalEvent.Choose -> {
                state.value = state.value.copy(
                    selectedOption = event.option
                )
            }

            is GraphModalEvent.HeartRate -> {
                state.update {
                    it.copy(
                        pulse = event.heartRate
                    )
                }
            }

            is GraphModalEvent.BloodSugar -> {
                state.update {
                    it.copy(
                        bloodSugar = event.bloodSugar
                    )
                }
            }

            is GraphModalEvent.Systolic -> {
                state.update {
                    it.copy(
                        systolic = event.systolic
                    )
                }
            }

            is GraphModalEvent.Diastolic -> {
                state.update {
                    it.copy(
                        diastolic = event.diastolic
                    )
                }
            }
        }
    }

    fun optionToStrings(option: Option): String {
        return when (option) {
            Option.HEART_RATE -> appContext.getString(R.string.heart_rate)
            Option.BLOOD_SUGAR -> appContext.getString(R.string.blood_sugar)
            Option.BLOOD_PRESSURE -> appContext.getString(R.string.blood_pressure)
        }
    }

}