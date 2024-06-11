package com.example.medcontrol.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity)

    @Query("SELECT * FROM medicines WHERE id = :medicineId")
    fun getMedicine(medicineId: Long): Flow<MedicineEntity>

    @Query("SELECT * FROM medicines")
    fun getAll(): Flow<List<MedicineEntity>>
}
