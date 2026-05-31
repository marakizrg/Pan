package com.example.pan.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, CheckedCourseEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PanDatabase : RoomDatabase() {

    abstract fun panDao(): PanDao

    companion object {
        @Volatile private var INSTANCE: PanDatabase? = null

        fun get(context: Context): PanDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PanDatabase::class.java,
                    "pan.db"
                )
                    // Tiny dataset — allow direct access so existing synchronous
                    // call sites (ViewModel init blocks, NavGraph) keep working.
                    .allowMainThreadQueries()
                    .build().also { INSTANCE = it }
            }
    }
}
