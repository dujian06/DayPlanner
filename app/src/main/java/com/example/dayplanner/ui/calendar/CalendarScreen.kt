package com.example.dayplanner.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dayplanner.data.model.CountdownEvent
import com.example.dayplanner.data.model.Schedule
import com.example.dayplanner.ui.components.AddScheduleDialog
import com.example.dayplanner.util.DateUtils
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = hiltViewModel()) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val schedules by viewModel.schedules.collectAsStateWithLifecycle()
    val countdowns by viewModel.countdowns.collectAsStateWithLifecycle()

    var showAdd by remember { mutableStateOf(false) }

    val base = YearMonth.of(2020, 1)
    val sel = DateUtils.epochToLocalDate(selectedDate)
    val initialPage = (sel.year - base.year) * 12 + (sel.monthValue - 1)
    val pagerState = rememberPagerState(initialPage = initialPage) { 200 }
    val currentYm = base.plusMonths(pagerState.currentPage.toLong())

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加")
            }
        }
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val ym = base.plusMonths(page.toLong())
                MonthGrid(
                    year = ym.year,
                    month = ym.monthValue,
                    selectedDate = selectedDate,
                    onSelect = viewModel::selectDate
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(
                DateUtils.formatCn(selectedDate) + " 的安排",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            LazyColumn(Modifier.fillMaxSize()) {
                items(schedules) { s -> ScheduleMiniCard(s) }
                val dueToday = countdowns.filter {
                    DateUtils.epochToLocalDate(it.targetDate) == sel
                }
                items(dueToday) { e -> CountdownMiniCard(e) }
                if (schedules.isEmpty() && dueToday.isEmpty()) {
                    item {
                        Text(
                            "这一天还没有安排，点击右下角 + 添加吧",
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddScheduleDialog(
            initialDate = selectedDate,
            onDismiss = { showAdd = false },
            onConfirm = {
                viewModel.addSchedule(it)
                showAdd = false
            }
        )
    }
}

@Composable
private fun MonthGrid(
    year: Int,
    month: Int,
    selectedDate: Long,
    onSelect: (Long) -> Unit
) {
    val cells = DateUtils.monthMatrix(year, month)
    val sel = DateUtils.epochToLocalDate(selectedDate)
    val today = DateUtils.epochToLocalDate(DateUtils.todayStart())

    Column {
        Text(
            "$year 年 $month 月",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row {
            listOf("日", "一", "二", "三", "四", "五", "六").forEach {
                Text(
                    it,
                    Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
        cells.chunked(7).forEach { week ->
            Row(Modifier.fillMaxWidth()) {
                week.forEach { d ->
                    Box(
                        Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clickable(enabled = d != null) {
                                d?.let {
                                    onSelect(
                                        DateUtils.dayStart(
                                            it.year, it.monthValue, it.dayOfMonth
                                        )
                                    )
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (d != null) {
                            val isSel = d == sel
                            val isToday = d == today
                            Box(
                                Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSel -> MaterialTheme.colorScheme.primary
                                            isToday -> MaterialTheme.colorScheme.primaryContainer
                                            else -> Color.Transparent
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${d.dayOfMonth}",
                                    color = if (isSel) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleMiniCard(s: Schedule) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(s.startTime, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            Column {
                Text(s.title, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                if (s.notes.isNotEmpty()) {
                    Text(s.notes, color = MaterialTheme.colorScheme.outline, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun CountdownMiniCard(e: CountdownEvent) {
    val left = DateUtils.daysUntil(e.targetDate)
    Card(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("🎯", fontSize = 20.sp)
            Column {
                Text(e.title, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text(
                    if (left > 0) "还有 $left 天" else "就是今天！",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 13.sp
                )
            }
        }
    }
}
