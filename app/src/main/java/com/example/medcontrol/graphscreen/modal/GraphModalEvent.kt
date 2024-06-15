package com.example.medcontrol.graphscreen.modal


sealed class GraphModalEvent {
    data class Choose(val option: Option): GraphModalEvent()
    data class HeartRate(val heartRate: Int?): GraphModalEvent()
    data class BloodSugar(val bloodSugar: Int?): GraphModalEvent()
    data class Systolic(val systolic: Int?): GraphModalEvent()
    data class Diastolic(val diastolic: Int?): GraphModalEvent()
}
