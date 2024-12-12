package com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation


import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlin.math.abs

@Composable
fun CollapsibleHeader(
    modifier: Modifier = Modifier,
    expandedHeight: Dp = 152.dp,
    collapsedHeight: Dp = 64.dp,
    headerContent: @Composable (progress: Float) -> Unit,
    content: @Composable () -> Unit
) {
    var headerHeightPx by remember { mutableFloatStateOf(0f) }
    var headerOffsetPx by remember { mutableFloatStateOf(0f) }

    val headerHeight = expandedHeight
    val headerHeightCollapsed = collapsedHeight

    val density = LocalDensity.current

    LaunchedEffect(headerHeight) {
        headerHeightPx = with(density) { headerHeight.toPx() }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = headerOffsetPx + delta
                headerOffsetPx = newOffset.coerceIn(-headerHeightPx + with(density) { headerHeightCollapsed.toPx() }, 0f)
                return Offset(0f, headerOffsetPx - newOffset)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        Column {
            val progress = 1 - abs(headerOffsetPx) / (headerHeightPx - with(density) { headerHeightCollapsed.toPx() })

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { headerHeight + (headerOffsetPx / density.density).dp })
                    .zIndex(1f),
                shadowElevation = if (headerOffsetPx < 0) 4.dp else 0.dp,
                color = MaterialTheme.colorScheme.background
            ) {
                headerContent(progress)
            }
            content()
        }
    }
}