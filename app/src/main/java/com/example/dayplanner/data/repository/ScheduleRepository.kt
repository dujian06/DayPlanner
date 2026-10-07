package com.example.dayplanner.data.repository

import com.example.dayplanner.data.local.ScheduleDao
import com.example.dayplanner.data.model.Schedule
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepository @Inject constructor(
    private val dao: ScheduleDao
) {
    fun getByDate(dayStart: Long): Flow<List<Schedule>> = dao.observeByDate(dayStart)

    suspend fun add(schedule: Schedule) = dao.insert(schedule)

    suspend fun remove(schedule: Schedule) = dao.delete(schedule)
}
