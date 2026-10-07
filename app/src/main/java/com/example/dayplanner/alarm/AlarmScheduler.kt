package com.example.dayplanner.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.dayplanner.data.model.Schedule
import com.example.dayplanner.util.DateUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 基于 AlarmManager 的本地闹钟调度。
 * 在 ScheduleViewModel.add 时调用，按"开始时间 - 提前量"触发广播。
 */
@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(schedule: Schedule) {
        val (h, m) = schedule.startTime.split(":").map { it.toIntOrNull() ?: 0 }
        val trigger = DateUtils.epochToLocalDate(schedule.date)
            .atTime(h, m)
            .minusMinutes(schedule.reminderOffsetMin.toLong())
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // 过去的时间不排程
        if (trigger <= System.currentTimeMillis()) return

        val pi = pendingIntent(schedule)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, trigger, pi
            )
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, trigger, pi)
        }
    }

    fun cancel(schedule: Schedule) {
        alarmManager.cancel(pendingIntent(schedule))
    }

    private fun pendingIntent(schedule: Schedule): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("title", schedule.title)
            putExtra("notes", schedule.notes)
        }
        return PendingIntent.getBroadcast(
            context,
            schedule.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
