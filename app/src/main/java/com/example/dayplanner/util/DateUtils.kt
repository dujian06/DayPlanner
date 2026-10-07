package com.example.dayplanner.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * 基于 java.time 的日期工具（minSdk 24 已通过 desugaring 支持）。
 * 所有"某天 0 点"统一用 epoch 毫秒表示，便于 Room 存储与比较。
 */
object DateUtils {
    private val zone: ZoneId get() = ZoneId.systemDefault()

    fun todayStart(): Long =
        LocalDate.now().atStartOfDay(zone).toInstant().toEpochMilli()

    fun dayStart(year: Int, month: Int, day: Int): Long =
        LocalDate.of(year, month, day).atStartOfDay(zone).toInstant().toEpochMilli()

    fun epochToLocalDate(epoch: Long): LocalDate =
        Instant.ofEpochMilli(epoch).atZone(zone).toLocalDate()

    /** 今天到目标日期（含当天为 0）的剩余天数。 */
    fun daysUntil(targetEpoch: Long): Long {
        val today = epochToLocalDate(todayStart())
        val target = epochToLocalDate(targetEpoch)
        return ChronoUnit.DAYS.between(today, target)
    }

    fun format(epoch: Long, pattern: String = "yyyy-MM-dd"): String =
        epochToLocalDate(epoch).format(DateTimeFormatter.ofPattern(pattern))

    fun formatCn(epoch: Long): String {
        val d = epochToLocalDate(epoch)
        return "${d.year}年${d.monthValue}月${d.dayOfMonth}日"
    }

    /** 生成月历网格（周日为第一列），空位用 null 占位。 */
    fun monthMatrix(year: Int, month: Int): List<LocalDate?> {
        val first = LocalDate.of(year, month, 1)
        // dayOfWeek: 周一=1..周日=7；转为以周日为 0 的偏移
        val leading = first.dayOfWeek.value % 7
        val daysInMonth = first.lengthOfMonth()
        val cells = mutableListOf<LocalDate?>()
        repeat(leading) { cells.add(null) }
        for (d in 1..daysInMonth) cells.add(LocalDate.of(year, month, d))
        while (cells.size % 7 != 0) cells.add(null)
        return cells
    }
}
