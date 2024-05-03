package com.filters.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EnteredDao {
    @Query("SELECT * FROM words WHERE typeFilter = :typeFilter LIMIT 1")
    suspend fun getEnteredData(typeFilter: String): EnteredData?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEnteredData(data: EnteredData)
}
