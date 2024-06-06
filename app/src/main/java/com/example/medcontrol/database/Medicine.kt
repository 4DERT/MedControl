package com.example.medcontrol.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.sql.Time
import java.time.LocalTime

data class TakeDate (
    val localTime: LocalTime,
    val isMonday: Boolean,
    val isTuesday: Boolean,
    val isWednesday: Boolean,
    val isThursday: Boolean,
    val isFriday: Boolean,
    val isSaturday: Boolean,
    val isSunday: Boolean
)

@Entity
@TypeConverters(Converters::class)
data class Medicine(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val dates: List<TakeDate>,
)
