package com.example.daily.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.daily.data.database.model.TaskDbModel
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTask(value: TaskDbModel)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertListTask(list: List<TaskDbModel>)

    @Query("SELECT * FROM TASK WHERE (DATE_START BETWEEN :startDay AND :endDay) AND (DATE_FINISH BETWEEN :startDay AND :endDay)")
    fun getListTaskByDay(startDay: Long, endDay: Long): Flow<List<TaskDbModel>>

    @Query("SELECT * FROM TASK")
    fun getListTask(): List<TaskDbModel>
}