package com.githu.stephenwanjala.composecontacts.core.presenation.utiles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

@Composable
fun rememberNestedScrollConnection(): NestedScrollConnection = remember {
    object : NestedScrollConnection {
        override suspend fun onPreFling(available: Velocity): Velocity {
            return available
        }

        override fun onPreScroll(available: androidx.compose.ui.geometry.Offset, source: NestedScrollSource): androidx.compose.ui.geometry.Offset {
            return androidx.compose.ui.geometry.Offset.Zero
        }
    }
}