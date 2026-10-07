package com.example.dayplanner.ui.holiday

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.dayplanner.util.DateUtils
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HolidayDetailScreen(
    navController: NavHostController,
    holidayId: String,
    viewModel: HolidayViewModel = hiltViewModel()
) {
    val holiday by viewModel.selected.collectAsStateWithLifecycle()

    LaunchedEffect(holidayId) { viewModel.load(holidayId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(holiday?.name ?: "详情") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { inner ->
        holiday?.let { h ->
            val daysLeft = run {
                val now = LocalDate.now()
                val target = LocalDate.of(now.year, h.month, h.day)
                val adjusted = if (target.isBefore(now)) target.plusYears(1) else target
                java.time.temporal.ChronoUnit.DAYS.between(now, adjusted)
            }
            Column(
                Modifier.fillMaxSize().padding(inner).verticalScroll(rememberScrollState())
            ) {
                // 顶部渐变大图（使用本地颜色占位，可替换为 AsyncImage 加载 assets 图片）
                Box(
                    Modifier.fillMaxWidth().height(240.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    Color(android.graphics.Color.parseColor(h.gradientStart)),
                                    Color(android.graphics.Color.parseColor(h.gradientEnd))
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(h.name, color = Color.White, fontSize = 32.sp)
                }

                Column(Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            h.category,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                        Text(
                            "· 距今还有 $daysLeft 天",
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 14.sp
                        )
                    }
                    Text(
                        h.description,
                        fontSize = 16.sp,
                        lineHeight = 26.sp,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        } ?: Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
