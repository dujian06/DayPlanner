package com.example.dayplanner.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dayplanner.data.model.CountdownEvent
import com.example.dayplanner.data.model.Schedule
import com.example.dayplanner.data.repository.CountdownRepository
import com.example.dayplanner.data.repository.ScheduleRepository
import com.example.dayplanner.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository,
    private val countdownRepository: CountdownRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(DateUtils.todayStart())
    val selectedDate: StateFlow<Long> = _selectedDate.asStateFlow()

    /** 选中日期当天的日程。 */
    val schedules: StateFlow<List<Schedule>> = _selectedDate
        .flatMapLatest { day -> scheduleRepository.getByDate(day) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** 全部倒计时（用于匹配"今天/选中日到期"的项目）。 */
    val countdowns: StateFlow<List<CountdownEvent>> = countdownRepository
        .getByCategory(CountdownEvent.CATEGORY_ALL)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDate(dayStart: Long) {
        _selectedDate.value = dayStart
    }

    fun addSchedule(schedule: Schedule) = viewModelScope.launch {
        scheduleRepository.add(schedule)
    }

    fun addCountdown(event: CountdownEvent) = viewModelScope.launch {
        countdownRepository.add(event)
    }

    fun removeCountdown(event: CountdownEvent) = viewModelScope.launch {
        countdownRepository.remove(event)
    }
}
