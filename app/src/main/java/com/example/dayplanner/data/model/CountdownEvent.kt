package com.example.dayplanner.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 倒数本事件（如生日、纪念日、目标日）。
 * targetDate 为当天 0 点的 epoch 毫秒。
 */
@Entity(tableName = "countdown_events")
data class CountdownEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val targetDate: Long,
    val category: String = CATEGORY_ALL,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val CATEGORY_ALL = "all"
        const val CATEGORY_LIFE = "life"
        const val CATEGORY_WORK = "work"
        const val CATEGORY_ANNIVERSARY = "anniversary"

        val CATEGORIES = listOf(
            CATEGORY_ALL to "全部",
            CATEGORY_LIFE to "生活",
            CATEGORY_WORK to "工作",
            CATEGORY_ANNIVERSARY to "纪念日"
        )
    }
}
