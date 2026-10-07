package com.example.dayplanner.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayplanner.alarm.AlarmScheduler
import com.example.dayplanner.data.model.Schedule
import com.example.dayplanner.data.repository.ScheduleRepository
import com.example.dayplanner.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    private val alarmScheduler: AlarmScheduler
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(DateUtils.todayStart())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    val schedules: StateFlow<List<Schedule>> = _selectedDate
        .flatMapLatest { day -> repository.getByDate(day) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDate(dayStart: Long) {
        _selectedDate.value = dayStart
    }

    fun add(schedule: Schedule) = viewModelScope.launch {
        repository.add(schedule)
        if (schedule.hasReminder) alarmScheduler.schedule(schedule)
    }

    fun remove(schedule: Schedule) = viewModelScope.launch {
        repository.remove(schedule)
        if (schedule.hasReminder) alarmScheduler.cancel(schedule)
    }
}
