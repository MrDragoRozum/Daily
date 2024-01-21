package com.example.daily.presentation.viewModel

sealed class StateTask {
    data object Loading: StateTask()
    data object Success: StateTask()
}