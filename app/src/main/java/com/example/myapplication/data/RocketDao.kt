package com.example.myapplication.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface RocketDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rockets: List<RocketEntity>)

    @Query("SELECT * FROM rockets")
    fun getAllRockets(): LiveData<List<RocketEntity>>

    @Query("DELETE FROM rockets")
    suspend fun deleteAll(): Int // Retorna el número de filas eliminadas

    @Delete
    suspend fun deleteRocket(rocket: RocketEntity)

    @Query("SELECT COUNT(*) FROM rockets")
    suspend fun getCount(): Int

    @Update
    suspend fun updateRocket(rocket: RocketEntity) // método de actualización

    @Query("SELECT * FROM rockets WHERE name = :name")
    fun getRocketByName(name: String): RocketEntity?

}
