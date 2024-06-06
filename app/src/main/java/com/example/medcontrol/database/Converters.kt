package com.example.medcontrol.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromTakeDateList(value: List<TakeDate>): String {
        val gson = Gson()
        val type = object : TypeToken<List<TakeDate>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toTakeDateList(value: String): List<TakeDate> {
        val gson = Gson()
        val type = object : TypeToken<List<TakeDate>>() {}.type
        return gson.fromJson(value, type)
    }
}
