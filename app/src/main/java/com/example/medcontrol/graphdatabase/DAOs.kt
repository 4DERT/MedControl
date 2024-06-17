package com.example.medcontrol.graphdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GraphDao {
    @Insert
    suspend fun insert(bloodPressure: BloodPressure)

    @Query("SELECT * FROM blood_pressure ORDER BY timestamp DESC")
    fun getAllBloodPressures(): Flow<List<BloodPressure>>

    @Insert
    suspend fun insert(pulse: Pulse)

    @Query("SELECT * FROM pulse ORDER BY timestamp DESC")
    fun getAllPulses(): Flow<List<Pulse>>

    @Insert
    suspend fun insert(bloodSugar: BloodSugar)

    @Query("SELECT * FROM blood_sugar ORDER BY timestamp DESC")
    fun getAllBloodSugars(): Flow<List<BloodSugar>>
}
