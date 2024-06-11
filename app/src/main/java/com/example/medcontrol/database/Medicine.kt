package com.example.medcontrol.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.UUID

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @TypeConverters(Converters::class) val notifications: List<NotificationEntity>
)

@Entity
data class NotificationEntity(
    val selectedDays: Map<DayOfWeek, Boolean>,
    val time: LocalTime,
    val isExpended: Boolean,
    @PrimaryKey val uuid: UUID
)