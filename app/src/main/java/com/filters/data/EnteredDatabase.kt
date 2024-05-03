package com.filters.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [EnteredData::class],
    version = 1
)
abstract class EnteredDatabase : RoomDatabase() {
    abstract fun enteredDao(): EnteredDao
}