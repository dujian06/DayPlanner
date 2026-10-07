package com.example.dayplanner.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * 通用滚轮选择器。
 *
 * 关键点（修复"滚动导致选择不一致"的核心）：
 *  - 滚动过程中只对【外部传入的 temp 状态】产生连续回调，不直接触碰业务主状态/数据库；
 *  - 由调用方在"确认"时才把最终选中的索引提交给真正的状态。
 *  - 使用 rememberSnapFlingBehavior 让滚轮停下时吸附到整项，避免半项错位。
 *
 * 由于首尾各加了两个空白项，列表中间恒为选中项，因此选中索引 = 列表首项索引。
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    items: List<String>,
    initialIndex: Int,
    onValueChanged: (index: Int, value: String) -> Unit,
    modifier: Modifier = Modifier,
    itemHeight: Dp = 44.dp
) {
    val safeInitial = initialIndex.coerceIn(0, (items.size - 1).coerceAtLeast(0))
    val listState: LazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = safeInitial
    )
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // 仅在"居中项"变化时才回调，避免每一帧都更新
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { it.coerceIn(0, (items.size - 1).coerceAtLeast(0)) }
            .distinctUntilChanged()
            .collect { index -> onValueChanged(index, items[index]) }
    }

    Box(modifier = modifier.height(itemHeight * 5)) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            item { Box(Modifier.height(itemHeight)) }
            item { Box(Modifier.height(itemHeight)) }
            items(items) { value ->
                Box(
                    Modifier.height(itemHeight).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(value, fontSize = 18.sp)
                }
            }
            item { Box(Modifier.height(itemHeight)) }
            item { Box(Modifier.height(itemHeight)) }
        }

        // 顶部/底部渐隐遮罩 + 中间选中条，纯视觉
        Box(
            Modifier
                .fillMaxWidth()
                .height(itemHeight * 5)
                .drawWithContent {
                    drawContent()
                    val h = size.height
                    drawRect(
                        brush = Brush.verticalGradient(
                            0f to Color.White.copy(alpha = 0.9f),
                            0.35f to Color.Transparent,
                            0.65f to Color.Transparent,
                            1f to Color.White.copy(alpha = 0.9f)
                        )
                    )
                }
        )
        Box(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                )
        )
    }
}
