package com.example.daily.data.mapper

import com.example.daily.data.database.models.TaskDbModel
import com.example.daily.data.externals.TaskJson
import com.example.daily.domain.models.Task
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class TaskMapper @Inject constructor(
    private val startCalendar: Calendar,
    private val endCalendar: Calendar,
    private val startDate: Date,
    private val endDate: Date
) {
    fun mapEntityToDbModel(task: Task): TaskDbModel = TaskDbModel(
        id = task.id,
        dateStart = task.dateStart,
        dateFinish = task.dateFinish,
        name = task.name,
        description = task.description
    )

    fun mapExternalToDbModel(taskJson: TaskJson): TaskDbModel = TaskDbModel(
        id = taskJson.id,
        dateStart = taskJson.dateStart,
        dateFinish = taskJson.dateFinish,
        name = taskJson.name,
        description = taskJson.description
    )

    fun mapDbModelToEntity(taskDbModel: TaskDbModel): Task {
        startDate.time = taskDbModel.dateStart
        endDate.time = taskDbModel.dateFinish

        startCalendar.time = startDate
        endCalendar.time = endDate

        val startHour = startCalendar.get(Calendar.HOUR_OF_DAY).toLong()
        val endHour = endCalendar.get(Calendar.HOUR_OF_DAY).toLong()

        return Task(
            id = taskDbModel.id,
            dateStart = startHour,
            dateFinish = endHour,
            name = taskDbModel.name,
            description = taskDbModel.description
        )
    }

    fun mapDbModelToExternal(taskDbModel: TaskDbModel): TaskJson = TaskJson(
        id = taskDbModel.id,
        dateStart = taskDbModel.dateStart,
        dateFinish = taskDbModel.dateFinish,
        name = taskDbModel.name,
        description = taskDbModel.description
    )
}