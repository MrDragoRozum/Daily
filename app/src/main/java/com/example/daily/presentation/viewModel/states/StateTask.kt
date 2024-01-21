package com.example.daily.presentation.viewModel.states

sealed class StateTask {
    data object Loading: StateTask()
    data object Success: StateTask()
}