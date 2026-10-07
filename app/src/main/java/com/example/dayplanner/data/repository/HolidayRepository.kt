package com.example.dayplanner.data.repository

import android.content.Context
import com.example.dayplanner.data.model.Holiday
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 节假日数据来自本地 assets/holidays.json，读取后在内存中匹配。
 * 不依赖网络，也不入库。
 */
@Singleton
class HolidayRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val holidays: List<Holiday> by lazy { loadFromAssets() }

    private fun loadFromAssets(): List<Holiday> {
        return try {
            context.assets.open("holidays.json").use { stream ->
                val text = stream.bufferedReader().readText()
                val array = JSONArray(text)
                (0 until array.length()).map { i ->
                    val o = array.getJSONObject(i)
                    Holiday(
                        id = o.getString("id"),
                        name = o.getString("name"),
                        month = o.getInt("month"),
                        day = o.getInt("day"),
                        category = o.getString("category"),
                        description = o.getString("description"),
                        gradientStart = o.getString("gradientStart"),
                        gradientEnd = o.getString("gradientEnd")
                    )
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getAll(): List<Holiday> = holidays

    fun getById(id: String): Holiday? = holidays.firstOrNull { it.id == id }

    /** 与今天的月/日匹配的节日（含节气）。 */
    fun getForToday(month: Int, day: Int): List<Holiday> =
        holidays.filter { it.month == month && it.day == day }
}
