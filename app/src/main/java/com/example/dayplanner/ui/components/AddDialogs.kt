package com.example.dayplanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dayplanner.data.model.CountdownEvent
import com.example.dayplanner.data.model.Schedule
import com.example.dayplanner.util.DateUtils
import java.time.DateTimeException
import java.time.LocalDate

private val YEARS = (2020..2030).map { "%04d".format(it) }
private val MONTHS = (1..12).map { "%02d".format(it) }
private val DAYS = (1..31).map { "%02d".format(it) }
private val HOURS = (0..23).map { "%02d".format(it) }
private val MINUTES = (0..59).map { "%02d".format(it) }

/**
 * 添加日程弹窗。
 *
 * 关键修复：年/月/日/时/分的滚轮在【滚动时只更新本地临时变量】
 * （tempYear/tempMonth/...），绝不直接写数据库或业务主状态；
 * 只有点击"确认"才做一次合法性校验并提交，从而彻底消除
 * "滚轮惯性滑动几百次回调导致选中与下方预览不一致"的问题。
 */
@Composable
fun AddScheduleDialog(
    initialDate: Long,
    onDismiss: () -> Unit,
    onConfirm: (Schedule) -> Unit
) {
    val init = DateUtils.epochToLocalDate(initialDate)

    var tempYear by remember { mutableIntStateOf(init.year) }
    var tempMonth by remember { mutableIntStateOf(init.monthValue) }
    var tempDay by remember { mutableIntStateOf(init.dayOfMonth) }
    var tempHour by remember { mutableIntStateOf(8) }
    var tempMinute by remember { mutableIntStateOf(0) }

    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var hasReminder by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val built = try {
                    LocalDate.of(tempYear, tempMonth, tempDay)
                } catch (e: DateTimeException) {
                    error = "日期无效（例如 2 月 30 日），请重选"
                    return@TextButton
                }
                if (title.isBlank()) {
                    error = "请填写标题"
                    return@TextButton
                }
                val start = "%02d:%02d".format(tempHour, tempMinute)
                onConfirm(
                    Schedule(
                        title = title.trim(),
                        date = DateUtils.dayStart(built.year, built.monthValue, built.dayOfMonth),
                        startTime = start,
                        endTime = start,
                        notes = notes.trim(),
                        hasReminder = hasReminder
                    )
                )
            }) { Text("确认") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
        title = { Text("添加日程") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    WheelPicker(YEARS, tempYear - 2020, { i, _ -> tempYear = 2020 + i }, Modifier.weight(1f))
                    WheelPicker(MONTHS, tempMonth - 1, { i, _ -> tempMonth = i + 1 }, Modifier.weight(1f))
                    WheelPicker(DAYS, tempDay - 1, { i, _ -> tempDay = i + 1 }, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    WheelPicker(HOURS, tempHour, { i, _ -> tempHour = i }, Modifier.weight(1f))
                    WheelPicker(MINUTES, tempMinute, { i, _ -> tempMinute = i }, Modifier.weight(1f))
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("备注（可选）") },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = hasReminder, onCheckedChange = { hasReminder = it })
                    Text("开启提醒（提前 5 分钟）")
                }
                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    )
}

/**
 * 添加倒计时事件弹窗。同样采用"临时状态 + 确认提交"模式。
 */
@Composable
fun AddCountdownDialog(
    initialDate: Long,
    onDismiss: () -> Unit,
    onConfirm: (CountdownEvent) -> Unit
) {
    val init = DateUtils.epochToLocalDate(initialDate)

    var tempYear by remember { mutableIntStateOf(init.year) }
    var tempMonth by remember { mutableIntStateOf(init.monthValue) }
    var tempDay by remember { mutableIntStateOf(init.dayOfMonth) }

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(CountdownEvent.CATEGORY_LIFE) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val built = try {
                    LocalDate.of(tempYear, tempMonth, tempDay)
                } catch (e: DateTimeException) {
                    error = "日期无效，请重选"
                    return@TextButton
                }
                if (title.isBlank()) {
                    error = "请填写标题"
                    return@TextButton
                }
                onConfirm(
                    CountdownEvent(
                        title = title.trim(),
                        targetDate = DateUtils.dayStart(built.year, built.monthValue, built.dayOfMonth),
                        category = category
                    )
                )
            }) { Text("确认") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
        title = { Text("添加倒计时") },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    WheelPicker(YEARS, tempYear - 2020, { i, _ -> tempYear = 2020 + i }, Modifier.weight(1f))
                    WheelPicker(MONTHS, tempMonth - 1, { i, _ -> tempMonth = i + 1 }, Modifier.weight(1f))
                    WheelPicker(DAYS, tempDay - 1, { i, _ -> tempDay = i + 1 }, Modifier.weight(1f))
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("标题，如：考研倒计时") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
                Text("分类", modifier = Modifier.padding(top = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    CountdownEvent.CATEGORIES.filter { it.first != CountdownEvent.CATEGORY_ALL }.forEach { (key, label) ->
                        FilterChip(
                            selected = category == key,
                            onClick = { category = key },
                            label = { Text(label) }
                        )
                    }
                }
                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    )
}
