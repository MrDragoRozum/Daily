package com.example.daily.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.daily.domain.models.Task
import com.example.daily.domain.usecase.AddTaskUseCase
import com.example.daily.presentation.models.TimeFromCalendarView
import com.example.daily.presentation.viewModel.WithoutTime.*
import com.example.daily.presentation.viewModel.states.StateTask
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject


typealias Time = TimeFromCalendarView

class TaskViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val calendarStartTask: Calendar,
    private val calendarEndTask: Calendar
) : ViewModel() {

    private val _state = MutableSharedFlow<StateTask>()
    val state = _state.asSharedFlow()

    fun add(task: Task, time: Time) {
        installCalendars(time, task)

        task.dateStart = calendarStartTask.timeInMillis
        task.dateFinish = calendarEndTask.timeInMillis

        addToDatabase(task)
    }

    private fun addToDatabase(task: Task) {
        viewModelScope.launch {
            addTaskUseCase(task)
            _state.emit(StateTask.Loading)
        }.apply {
            viewModelScope.launch {
                join()
                _state.emit(StateTask.Success)
            }
        }
    }

    private fun installCalendars(time: Time, task: Task) {
        with(time) {
            calendarStartTask.apply {
                set(
                    year,
                    month,
                    dayOfMonth,
                    task.dateStart.toInt(),
                    WITHOUT_MINUTE.zero,
                    WITHOUT_SECOND.zero
                )
                set(Calendar.MILLISECOND, WITHOUT_MILLISECOND.zero)
            }

            calendarEndTask.apply {
                set(
                    year,
                    month,
                    dayOfMonth,
                    task.dateFinish.toInt(),
                    WITHOUT_MINUTE.zero,
                    WITHOUT_SECOND.zero
                )
                set(Calendar.MILLISECOND, WITHOUT_MILLISECOND.zero)
            }
        }
    }
}
