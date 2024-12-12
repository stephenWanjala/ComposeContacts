package com.githu.stephenwanjala.composecontacts.core.presenation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex


@Composable
fun StickyHeader(
    modifier: Modifier = Modifier,
    headerContent: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f),
                shadowElevation = 4.dp,
                color = Color.White
            ) {
                headerContent()
            }
            content()
        }
    }
}