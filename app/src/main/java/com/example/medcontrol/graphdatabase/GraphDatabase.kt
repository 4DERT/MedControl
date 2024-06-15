package com.example.medcontrol.graphdatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BloodPressure::class, Pulse::class, BloodSugar::class], version = 1)
abstract class GraphDatabase : RoomDatabase() {
    abstract fun graphDao(): GraphDao

    companion object {
        @Volatile
        private var INSTANCE: GraphDatabase? = null

        fun getDatabase(context: Context): GraphDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GraphDatabase::class.java,
                    "health_monitoring_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}