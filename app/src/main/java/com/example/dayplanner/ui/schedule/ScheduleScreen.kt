package com.example.dayplanner.ui.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dayplanner.data.model.Schedule
import com.example.dayplanner.ui.components.AddScheduleDialog
import com.example.dayplanner.util.DateUtils

@Composable
fun ScheduleScreen(viewModel: ScheduleViewModel = hiltViewModel()) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加日程")
            }
        }
    ) { inner ->
        Column(
            Modifier.fillMaxSize().padding(inner).padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text("今日安排", style = MaterialTheme.typography.headlineSmall)
            Text(
                DateUtils.formatCn(selectedDate),
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (schedules.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("还没有日程，点击右下角 + 添加", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn {
                    items(schedules) { s -> TimelineItem(s) }
                }
            }
        }
    }

    if (showAdd) {
        AddScheduleDialog(
            initialDate = selectedDate,
            onDismiss = { showAdd = false },
            onConfirm = {
                viewModel.add(it)
                showAdd = false
            }
        )
    }
}

@Composable
private fun TimelineItem(schedule: Schedule) {
    Row(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        // 左侧时间轴：时间 + 竖线 + 圆点
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(64.dp)
        ) {
            Text(
                schedule.startTime,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            Box(
                Modifier.width(2.dp).weight(1f, fill = true)
                    .background(Color.LightGray)
            )
            Box(
                Modifier.size(12.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }

        Spacer(Modifier.width(12.dp))

        // 右侧内容卡片
        Card(
            Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(schedule.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (schedule.notes.isNotEmpty()) {
                    Text(schedule.notes, fontSize = 14.sp, color = MaterialTheme.colorScheme.outline)
                }
                if (schedule.hasReminder) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 6.dp)
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "已开启提醒（提前 ${schedule.reminderOffsetMin} 分钟）",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
