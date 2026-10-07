package com.example.dayplanner.ui.countdown

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dayplanner.data.model.CountdownEvent
import com.example.dayplanner.ui.components.AddCountdownDialog
import com.example.dayplanner.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountdownScreen(viewModel: CountdownViewModel = hiltViewModel()) {
    val category by viewModel.category.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    var showAdd by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAdd = true }) {
                Icon(Icons.Default.Add, contentDescription = "添加倒计时")
            }
        }
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text("倒数本", style = MaterialTheme.typography.headlineSmall)
            // 分类标签
            androidx.compose.foundation.layout.Row(
                Modifier.padding(vertical = 8.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                CountdownEvent.CATEGORIES.forEach { (key, label) ->
                    FilterChip(
                        selected = category == key,
                        onClick = { viewModel.setCategory(key) },
                        label = { Text(label) }
                    )
                }
            }

            if (events.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("暂无该分类的倒计时", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn {
                    items(events, key = { it.id }) { event ->
                        SwipeToDismissItem(
                            event = event,
                            onDelete = { viewModel.remove(event) }
                        )
                    }
                }
            }
        }
    }

    if (showAdd) {
        AddCountdownDialog(
            initialDate = DateUtils.todayStart(),
            onDismiss = { showAdd = false },
            onConfirm = {
                viewModel.add(it)
                showAdd = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeToDismissItem(
    event: CountdownEvent,
    onDelete: () -> Unit
) {
    val dismissState = rememberDismissState(
        confirmValueChange = { value ->
            if (value == androidx.compose.material3.DismissValue.DismissedToStart ||
                value == androidx.compose.material3.DismissValue.DismissedToEnd
            ) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismiss(
        state = dismissState,
        background = {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE53935))
                    .padding(horizontal = 20.dp),
                contentAlignment = if (dismissState.dismissDirection == androidx.compose.material3.DismissDirection.EndToStart)
                    Alignment.CenterEnd else Alignment.CenterStart
            ) {
                Icon(Icons.Default.Delete, contentDescription = "删除", tint = Color.White)
            }
        },
        dismissContent = {
            val left = DateUtils.daysUntil(event.targetDate)
            Card(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(event.title, style = MaterialTheme.typography.titleMedium)
                    Text(
                        DateUtils.formatCn(event.targetDate),
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 13.sp
                    )
                    Text(
                        if (left > 0) "还剩 $left 天" else "就是今天！",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}
