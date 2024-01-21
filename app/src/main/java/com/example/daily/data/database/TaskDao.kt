package com.example.daily.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.daily.data.database.models.TaskDbModel


@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(value: TaskDbModel)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertListTask(list: List<TaskDbModel>)

    @Query("SELECT * FROM TASK WHERE (DATE_START BETWEEN :startDay AND :endDay) AND (DATE_FINISH BETWEEN :startDay AND :endDay)")
    suspend fun getListTaskByDay(startDay: Long, endDay: Long): List<TaskDbModel>

    @Query("SELECT * FROM TASK")
    suspend fun getListTask(): List<TaskDbModel>
}