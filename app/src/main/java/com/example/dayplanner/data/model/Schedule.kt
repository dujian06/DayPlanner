package com.example.dayplanner.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 小工具/日程：有明确开始、结束时间的当天安排。
 * date 为当天 0 点 epoch 毫秒；startTime/endTime 为 "HH:mm"。
 */
@Entity(tableName = "schedules")
data class Schedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: Long,
    val startTime: String,
    val endTime: String,
    val notes: String = "",
    val hasReminder: Boolean = false,
    val reminderOffsetMin: Int = 5
)
