package com.example.daily.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.daily.data.database.models.TaskDbModel

@Database(entities = [TaskDbModel::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    companion object {
        private var database: TaskDatabase? = null
        private const val DB_NAME = "tasks.db"
        private val LOCK = Any()

        fun getInstance(context: Context): TaskDatabase {
            database?.let { return it }
            synchronized(LOCK) {
                database?.let { return it }
                val instance = Room.databaseBuilder(
                    context,
                    TaskDatabase::class.java,
                    DB_NAME
                ).build()
                database = instance
                return instance
            }
        }
    }

    abstract fun taskDao(): TaskDao
}