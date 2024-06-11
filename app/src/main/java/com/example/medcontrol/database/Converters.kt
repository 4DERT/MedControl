package com.example.medcontrol.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromNotificationEntityList(notifications: List<NotificationEntity>): String {
        return gson.toJson(notifications)
    }

    @TypeConverter
    fun toNotificationEntityList(data: String): List<NotificationEntity> {
        val listType = object : TypeToken<List<NotificationEntity>>() {}.type
        return gson.fromJson(data, listType)
    }

    @TypeConverter
    fun fromDayOfWeekMap(selectedDays: Map<DayOfWeek, Boolean>): String {
        return gson.toJson(selectedDays)
    }

    @TypeConverter
    fun toDayOfWeekMap(data: String): Map<DayOfWeek, Boolean> {
        val mapType = object : TypeToken<Map<DayOfWeek, Boolean>>() {}.type
        return gson.fromJson(data, mapType)
    }

    @TypeConverter
    fun fromLocalTime(time: LocalTime): String {
        return time.format(DateTimeFormatter.ISO_LOCAL_TIME)
    }

    @TypeConverter
    fun toLocalTime(timeString: String): LocalTime {
        return LocalTime.parse(timeString, DateTimeFormatter.ISO_LOCAL_TIME)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID): String {
        return uuid.toString()
    }

    @TypeConverter
    fun toUUID(uuidString: String): UUID {
        return UUID.fromString(uuidString)
    }
}

