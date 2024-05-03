package com.filters

import android.app.Application
import androidx.room.Room
import com.filters.data.EnteredDatabase

class App : Application() {
    lateinit var db: EnteredDatabase

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            EnteredDatabase::class.java,
            "db"
        )
            //            .fallbackToDestructiveMigration()
            .build()
    }
}