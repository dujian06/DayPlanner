package com.example.dayplanner.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dayplanner.data.model.Schedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM schedules WHERE date = :dayStart ORDER BY startTime ASC")
    fun observeByDate(dayStart: Long): Flow<List<Schedule>>

    @Query("SELECT * FROM schedules ORDER BY date ASC, startTime ASC")
    fun observeAll(): Flow<List<Schedule>>

    @Query("SELECT * FROM schedules WHERE hasReminder = 1")
    suspend fun getRemindable(): List<Schedule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: Schedule)

    @Delete
    suspend fun delete(schedule: Schedule)
}
