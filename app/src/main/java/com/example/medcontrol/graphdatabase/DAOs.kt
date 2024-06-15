package com.example.medcontrol.graphdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GraphDao {
    @Insert
    suspend fun insert(bloodPressure: BloodPressure)

    @Query("SELECT * FROM blood_pressure ORDER BY timestamp DESC")
    suspend fun getAllBloodPressures(): List<BloodPressure>

    @Insert
    suspend fun insert(pulse: Pulse)

    @Query("SELECT * FROM pulse ORDER BY timestamp DESC")
    suspend fun getAllPulses(): List<Pulse>

    @Insert
    suspend fun insert(bloodSugar: BloodSugar)

    @Query("SELECT * FROM blood_sugar ORDER BY timestamp DESC")
    suspend fun getAllBloodSugars(): List<BloodSugar>
}
