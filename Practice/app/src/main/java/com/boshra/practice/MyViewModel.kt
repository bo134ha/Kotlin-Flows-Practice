package com.boshra.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MyViewModel: ViewModel() {

    // cold flow
    val countdownFlow: Flow<Int> = flow {
        var count = 100
        while (count >= 0) {
            emit(count)
            delay(1000L)
            count--
        }
    }

    val cryptoPriceFlow: Flow<Double> = flow {
        var basePrice = 65000.0
        while (true) {
            val change = (-500..500).random().toDouble()
            basePrice += change
            emit(basePrice)
            delay(2000L)
        }
    }

    // hot flow
    private val _uiState = MutableStateFlow<TasksUiState>(TasksUiState.Loading)
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    init {
        loadTasksSimulated()
    }

    private fun loadTasksSimulated() {
        viewModelScope.launch {
            delay(3000L)

            val dummyTasks = listOf("practice android", "flow examples", "go to collage")

            _uiState.value = TasksUiState.Success(dummyTasks)
        }
    }

    //

    private val _uiEvent = MutableSharedFlow<OtpEvent>()

    val uiEvent: SharedFlow<OtpEvent> = _uiEvent.asSharedFlow()

    fun verifyOtpCode(code: String) {
        viewModelScope.launch {
            if (code.isEmpty()) {
                _uiEvent.emit(OtpEvent.ShowSnackbarMessage("Please enter the code first"))
            } else if (code == "1234") {
                _uiEvent.emit(OtpEvent.NavigateToHomeScreen)
            } else {
                _uiEvent.emit(OtpEvent.ShowSnackbarMessage("try again"))
            }
        }
    }
}

sealed interface TasksUiState {
    object Loading : TasksUiState
    data class Success(val tasksList: List<String>) : TasksUiState
    data class Error(val errorMessage: String) : TasksUiState
}

sealed interface OtpEvent {
    data class ShowSnackbarMessage(val message: String) : OtpEvent
    object NavigateToHomeScreen : OtpEvent
}