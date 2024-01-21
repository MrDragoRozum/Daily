package com.example.daily.presentation.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daily.domain.usecase.ExportTasksUseCase
import com.example.daily.domain.usecase.GetListTaskSpecificDay
import com.example.daily.domain.usecase.ImportTasksUseCase
import com.example.daily.domain.usecase.RequestNewListTaskSpecificDay
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.Calendar
import javax.inject.Inject

class MainViewModel @Inject constructor(
    getListTaskSpecificDay: GetListTaskSpecificDay,
    private val exportTasksUseCase: ExportTasksUseCase,
    private val importTasksUseCase: ImportTasksUseCase,
    private val requestNewListTaskSpecificDay: RequestNewListTaskSpecificDay,
    private val calendar: Calendar,
    private val rawOffset: Int
) : ViewModel() {

    private var isNotException = true

    private val exception = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            error.emit(StateMain.Error)
            isNotException = false
            Log.d("ViewModelException", "$throwable")
        }
    }

    private val loading = MutableSharedFlow<StateMain>()
    private val success = MutableSharedFlow<StateMain>()
    private val error = MutableSharedFlow<StateMain>()

    val stateMain = getListTaskSpecificDay.invoke()
        .map { StateMain.Result(it) }
        .mergeWith(loading, error, success)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(FIVE_SECOND), StateMain.Loading)

        // Может быть тут собака зарыта
    fun refreshTasks(year: Int, month: Int, dayOfMonth: Int) {
        viewModelScope.launch {
            loading.emit(StateMain.Loading)
            calendar.set(year, month, dayOfMonth)
            Timestamp(calculateTime(calendar.timeInMillis)).also { startDay ->
                Timestamp(startDay.time + DAY_IN_MILLIS).also { endDay ->
                    requestNewListTaskSpecificDay(startDay.time, endDay.time)
                }
            }
        }
    }

    fun import(uri: Uri?) {
        uri?.let {
            viewModelScope.launch(exception) {
                importTasksUseCase(uri.toString())
            }.also { returnSuccess(it) }
        }
    }

    fun export(uri: Uri?) {
        uri?.let {
            viewModelScope.launch(exception) {
                exportTasksUseCase(uri.toString())
            }.also { returnSuccess(it) }
        }
    }

    private fun returnSuccess(coroutine: Job) {
        viewModelScope.launch {
            coroutine.join()
            if (isNotException) {
                success.emit(StateMain.Success)
            }
            isNotException = true
        }
    }

    private fun <T> Flow<T>.mergeWith(vararg another: Flow<T>) = merge(this, *another)
    private fun calculateTime(time: Long): Long = (time / DAY_IN_MILLIS) * DAY_IN_MILLIS - rawOffset

    companion object {
        private const val DAY_IN_MILLIS = 86400000
        private const val FIVE_SECOND = 5_000L
    }
}