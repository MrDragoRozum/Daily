package com.example.daily.presentation

import com.example.daily.domain.models.Task

sealed class State {
    data object Error : State()
    data object Loading : State()
    class Result(val list: List<Task>): State()
}