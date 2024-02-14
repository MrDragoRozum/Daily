package com.example.daily.data.repository

import android.content.Context
import android.net.Uri
import com.example.daily.data.database.TaskDao
import com.example.daily.data.externals.TaskJson
import com.example.daily.data.mapper.TaskMapper
import com.example.daily.data.models.TodayDayInTimestamps
import com.example.daily.domain.models.Task
import com.example.daily.domain.repository.DailyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import javax.inject.Inject

class DailyRepositoryImpl @Inject constructor(
    private val context: Context,
    private val dao: TaskDao,
    private val mapper: TaskMapper,
    private val todayDayInTimestamps: TodayDayInTimestamps,
    private val dispatcherIO: CoroutineDispatcher
) : DailyRepository {

    private val refreshListTask = MutableSharedFlow<Unit>()

    override suspend fun requestNewListTaskSpecificDay(startDay: Long, endDay: Long) {
        todayDayInTimestamps.startDay.time = startDay
        todayDayInTimestamps.endDay.time = endDay
        update()
    }

    override suspend fun addTask(params: Task) {
        dao.insertTask(mapper.mapEntityToDbModel(params))
        update()
    }

    override fun getListTaskSpecificDay(): Flow<List<Task>> = flow {
        emit(getMappedListFromDbToEntity())
        refreshListTask.collect {
            emit(getMappedListFromDbToEntity())
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun importTasks(uri: String) {
        withContext(dispatcherIO) {
            val uriFromString = Uri.parse(uri)
            context.contentResolver.openInputStream(uriFromString)?.let { inputStreamNotNull ->
                inputStreamNotNull.use { inputStream ->
                    Json.decodeFromStream<List<TaskJson>>(inputStream).also { list ->
                        dao.insertListTask(list.map { mapper.mapExternalToDbModel(it) })
                    }
                }
            }
            update()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun exportTasks(uri: String) {
        withContext(dispatcherIO) {
            val uriFromString = Uri.parse(uri)
            context.contentResolver.openOutputStream(uriFromString)?.let { outputStreamNotNull ->
                outputStreamNotNull.use { outputStream ->
                    val list = dao.getListTask().map { mapper.mapDbModelToExternal(it) }
                    Json.encodeToStream(list, outputStream)
                }
            }
        }
    }

    private suspend fun update() {
        refreshListTask.emit(Unit)
    }

    private suspend fun getMappedListFromDbToEntity() = dao.getListTaskByDay(
        todayDayInTimestamps.startDay.time,
        todayDayInTimestamps.endDay.time
    ).map { mapper.mapDbModelToEntity(it) }
}