package com.example.medcontrol.graphdatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_pressure")
data class BloodPressure(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val systolic: Int,
    val diastolic: Int,
    val timestamp: Long
)

@Entity(tableName = "pulse")
data class Pulse(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pulse: Int,
    val timestamp: Long
)

@Entity(tableName = "blood_sugar")
data class BloodSugar(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bloodSugar: Float,
    val timestamp: Long
)