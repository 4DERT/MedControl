package com.example.medcontrol.graphscreen

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medcontrol.graphdatabase.BloodPressure
import com.example.medcontrol.graphdatabase.BloodSugar
import com.example.medcontrol.graphdatabase.GraphDao
import com.example.medcontrol.graphdatabase.Pulse
import com.example.medcontrol.graphscreen.modal.GraphModalState
import com.example.medcontrol.graphscreen.modal.Option
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class GraphScreenViewModel(
    private val graphDao: GraphDao,
    application: Application
) : AndroidViewModel(application) {

    private val appContext: Context = getApplication<Application>().applicationContext

    val state = MutableStateFlow<GraphScreenState>(GraphScreenState.Empty)
    val fabState = MutableStateFlow(GraphScreenViewItem(false))

    init {
        viewModelScope.launch {
            state.value = GraphScreenState.Loading

            val pulseData = graphDao.getAllPulses()
            val bloodSugarData = graphDao.getAllBloodSugars()
            val bloodPressureData = graphDao.getAllBloodPressures()

            if(pulseData.size < 2 && bloodSugarData.size < 2 && bloodPressureData.size < 2) {
                state.value = GraphScreenState.Empty
                return@launch
            }


            // Create lists of Entry objects
            val heartRateEntries =
                pulseData.map { Entry(it.timestamp.toFloat(), it.pulse.toFloat()) }
            val bloodSugarEntries =
                bloodSugarData.map { Entry(it.timestamp.toFloat(), it.bloodSugar) }
            val systolicEntries =
                bloodPressureData.map { Entry(it.timestamp.toFloat(), it.systolic.toFloat()) }
            val diastolicEntries =
                bloodPressureData.map { Entry(it.timestamp.toFloat(), it.diastolic.toFloat()) }

            // Create GraphDataViewItem
            val graphDataViewItem = GraphDataViewItem(
                hearthRateData = heartRateEntries,
                bloodSugarData = bloodSugarEntries,
                bloodPressureSystolicData = systolicEntries,
                bloodPressureDiastolicData = diastolicEntries
            )

            // Update state with the new data
            state.value = GraphScreenState.Success(graphDataViewItem)
        }
    }

    fun showModal() {
        fabState.update { it.copy(true) }
    }

    fun hideModal() {
        fabState.update { it.copy(false) }
    }

    fun insertData(data: GraphModalState) {
        viewModelScope.launch {
            val localTime = LocalTime.now()
            val currentDate = LocalDate.now()
            val dateTime = LocalDateTime.of(currentDate, localTime)
            val unixTime = dateTime.toEpochSecond(ZoneId.systemDefault().rules.getOffset(dateTime))

            when (data.selectedOption) {
                Option.HEART_RATE -> {
                    if (data.pulse == null)
                        return@launch

                    val pulse = Pulse(
                        pulse = data.pulse,
                        timestamp = unixTime
                    )
                    graphDao.insert(pulse)
                }

                Option.BLOOD_SUGAR -> {
                    if (data.bloodSugar == null)
                        return@launch

                    val bloodSugar = BloodSugar(
                        bloodSugar = data.bloodSugar.toFloat(),
                        timestamp = unixTime
                    )
                    graphDao.insert(bloodSugar)
                }

                Option.BLOOD_PRESSURE -> {
                    if (data.systolic == null || data.diastolic == null)
                        return@launch

                    val bloodPressure = BloodPressure(
                        systolic = data.systolic,
                        diastolic = data.diastolic,
                        timestamp = unixTime
                    )
                    graphDao.insert(bloodPressure)
                }

                null -> {}
            }
        }
    }


}