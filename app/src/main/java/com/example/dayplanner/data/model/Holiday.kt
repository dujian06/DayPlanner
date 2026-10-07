package com.example.dayplanner.data.model

/**
 * 节假日/节气。数据来自本地 assets/holidays.json，不入库。
 * month/day 用于与"今天"匹配；gradientStart/End 为详情页渐变占位的十六进制色值。
 */
data class Holiday(
    val id: String,
    val name: String,
    val month: Int,
    val day: Int,
    val category: String,
    val description: String,
    val gradientStart: String,
    val gradientEnd: String
)
