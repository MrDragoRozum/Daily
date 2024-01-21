package com.example.daily.presentation.viewModel

import com.example.daily.domain.models.Task

sealed class StateMain {
    data object Error : StateMain()
    data object Loading : StateMain()
    data object Success: StateMain()
    class Result(val list: List<Task>) : StateMain()
}