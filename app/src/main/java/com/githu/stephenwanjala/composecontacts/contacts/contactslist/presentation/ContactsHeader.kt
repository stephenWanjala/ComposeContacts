package com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ContactsHeader(
    progress: Float,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Expanded header content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .alpha(progress)
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Contacts",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${(350 * progress).toInt()} contacts",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Collapsed header content
        Row(
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, "Menu")
            }

            Text(
                text = "Contacts",
                modifier = Modifier.alpha(1 - progress),
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, "Search")
            }
        }
    }
}