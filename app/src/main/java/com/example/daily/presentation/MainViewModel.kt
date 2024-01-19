package com.example.daily.presentation

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daily.domain.usecase.ExportTasksUseCase
import com.example.daily.domain.usecase.GetListTaskSpecificDay
import com.example.daily.domain.usecase.ImportTasksUseCase
import com.example.daily.domain.usecase.RequestNewListTaskSpecificDay
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.Calendar
import javax.inject.Inject

class MainViewModel @Inject constructor(
    getListTaskSpecificDay: GetListTaskSpecificDay,
    private val exportTasksUseCase: ExportTasksUseCase,
    private val importTasksUseCase: ImportTasksUseCase,
    private val requestNewListTaskSpecificDay: RequestNewListTaskSpecificDay,
    private val calendar: Calendar
) : ViewModel() {

    private val exception = CoroutineExceptionHandler { _, throwable ->
        viewModelScope.launch {
            error.emit(State.Error)
            Log.d("ViewModelException", "$throwable")
        }
    }

    private val loading = MutableStateFlow<State>(State.Loading)
    private val error = MutableSharedFlow<State>()

    val state = getListTaskSpecificDay.invoke()
        .map { State.Result(it) }
        .mergeWith(loading, error)
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    fun refreshTasks(year: Int, month: Int, dayOfMonth: Int) {
        loading.value = State.Loading
        calendar.set(year, month, dayOfMonth)
        Timestamp(calculateTime(calendar.timeInMillis)).also { startDay ->
            Timestamp(startDay.time + DAY_IN_MILLIS).also { endDay ->
                viewModelScope.launch {
                    requestNewListTaskSpecificDay(startDay.time, endDay.time)
                }
            }
        }
    }

    fun import(uri: Uri?) {
        uriNotNull(uri)
        viewModelScope.launch(exception) {
            importTasksUseCase(uri.toString())
        }
    }

    fun export(uri: Uri?) {
        uriNotNull(uri)
        viewModelScope.launch(exception) {
            exportTasksUseCase(uri.toString())
        }
    }

    private fun uriNotNull(uri: Uri?) {
        if (uri == null) {
            loading.value = State.Error
            return
        }
    }

    private fun <T> Flow<T>.mergeWith(vararg another: Flow<T>) = merge(this, *another)
    private fun calculateTime(time: Long): Long = (time / DAY_IN_MILLIS) * DAY_IN_MILLIS

    companion object {
        private const val DAY_IN_MILLIS = 86400000
    }
}