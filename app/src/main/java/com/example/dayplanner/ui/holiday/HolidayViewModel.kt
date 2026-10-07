package com.example.dayplanner.ui.holiday

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayplanner.data.model.Holiday
import com.example.dayplanner.data.repository.HolidayRepository
import com.example.dayplanner.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HolidayViewModel @Inject constructor(
    private val repository: HolidayRepository
) : ViewModel() {

    private val _selected = MutableStateFlow<Holiday?>(null)
    val selected: StateFlow<Holiday?> = _selected.asStateFlow()

    val allHolidays: StateFlow<List<Holiday>> =
        MutableStateFlow(repository.getAll()).asStateFlow()

    val todayHolidays: StateFlow<List<Holiday>> = run {
        val today = DateUtils.epochToLocalDate(DateUtils.todayStart())
        MutableStateFlow(repository.getForToday(today.monthValue, today.dayOfMonth)).asStateFlow()
    }

    fun load(id: String) {
        viewModelScope.launch {
            _selected.value = repository.getById(id)
        }
    }
}
