package com.example.dayplanner.ui.holiday

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.dayplanner.data.model.Holiday

@Composable
fun HolidayScreen(
    navController: NavHostController,
    viewModel: HolidayViewModel = hiltViewModel()
) {
    val today by viewModel.todayHolidays.collectAsStateWithLifecycle()
    val all by viewModel.allHolidays.collectAsStateWithLifecycle()

    Column(
        Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text("节假日", style = MaterialTheme.typography.headlineSmall)

        if (today.isNotEmpty()) {
            Text("今天是", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            today.forEach { h ->
                HolidayBanner(h, onClick = { navController.navigate("holiday/${h.id}") })
            }
        }

        Text("全部节日", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp))
        LazyColumn {
            items(all) { h ->
                HolidayRow(h, onClick = { navController.navigate("holiday/${h.id}") })
            }
        }
    }
}

@Composable
private fun HolidayBanner(h: Holiday, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.linearGradient(
                        listOf(Color(android.graphics.Color.parseColor(h.gradientStart)),
                            Color(android.graphics.Color.parseColor(h.gradientEnd)))
                    )
                ),
            contentAlignment = Alignment.CenterStart
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(h.name, color = Color.White, fontSize = 24.sp)
                Text(h.category, color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun HolidayRow(h: Holiday, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier.size(44.dp).clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(android.graphics.Color.parseColor(h.gradientStart)),
                                Color(android.graphics.Color.parseColor(h.gradientEnd)))
                        )
                    )
            )
            Column {
                Text(h.name, style = MaterialTheme.typography.titleMedium)
                Text("${h.month} 月 ${h.day} 日 · ${h.category}",
                    color = MaterialTheme.colorScheme.outline, fontSize = 13.sp)
            }
        }
    }
}
