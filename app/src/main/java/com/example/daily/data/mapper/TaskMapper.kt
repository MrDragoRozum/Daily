package com.example.daily.data.mapper

import com.example.daily.data.database.model.TaskDbModel
import com.example.daily.data.external.TaskJson
import com.example.daily.domain.models.Task
import javax.inject.Inject

// TODO: Переделать маппер
class TaskMapper @Inject constructor() {
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

    fun mapDbModelToEntity(taskDbModel: TaskDbModel): Task = Task(
        id = taskDbModel.id,
        dateStart = taskDbModel.dateStart,
        dateFinish = taskDbModel.dateFinish,
        name = taskDbModel.name,
        description = taskDbModel.description
    )

    fun mapDbModelToExternal(taskDbModel: TaskDbModel): TaskJson = TaskJson(
        id = taskDbModel.id,
        dateStart = taskDbModel.dateStart,
        dateFinish = taskDbModel.dateFinish,
        name = taskDbModel.name,
        description = taskDbModel.description
    )
}