package com.example.medcontrol.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MedicineDao {
    @Insert
    suspend fun insert(medicine: Medicine)

    @Query("SELECT * FROM medicine")
    suspend fun getAll(): List<Medicine>
}