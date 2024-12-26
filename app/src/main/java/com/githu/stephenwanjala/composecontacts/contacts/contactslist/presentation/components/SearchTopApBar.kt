package com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchTopAppBar(
    title: String,
    isSearchActive: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSearchDismiss: () -> Unit,
    onNavigateUp: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    AnimatedContent(
        targetState = isSearchActive, transitionSpec = {
            fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically()
        }, label = "SearchTopAppBarTransition"
    ) { searchActive ->
        if (searchActive) {
            TopAppBar(title = {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    placeholder = { Text("Search Contacts...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }, navigationIcon = {
                IconButton(onClick = onSearchDismiss) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Close search"
                    )
                }
            }, actions = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            }, scrollBehavior = scrollBehavior
            )
        } else {
            TopAppBar(title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }, navigationIcon = {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        imageVector = Icons.Outlined.Close, contentDescription = "Close"
                    )
                }
            }, actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Outlined.Search, contentDescription = "Search"
                    )
                }
            }, scrollBehavior = scrollBehavior
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableLargeTopAppBar(
    numberOfContacts: Int,
    isCollapsed: Boolean,
    searchQuery: String,
    isSearchActive: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onSearchDismiss: () -> Unit,
    onSettingsClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    searchScrollBehavior: TopAppBarScrollBehavior? = null,
) {
    AnimatedContent(
        targetState = isSearchActive, transitionSpec = {
            fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically()
        }, label = "SearchableLargeTopAppBarTransition"
    ) { searchActive ->
        if (searchActive) {
            TopAppBar(title = {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    placeholder = { Text("Search Contacts...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }, navigationIcon = {
                IconButton(onClick = onSearchDismiss) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Close search"
                    )
                }
            }, actions = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Outlined.Clear,
                            contentDescription = "Clear search"
                        )
                    }
                }
            }, scrollBehavior = searchScrollBehavior
            )
        } else {
            LargeTopAppBar(title = {
                Column(
                    modifier = Modifier.animateContentSize()
                ) {
                    if (isCollapsed) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Contacts",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "$numberOfContacts Contacts",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        Column {
                            Text(
                                text = "Contacts", style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "$numberOfContacts Contacts",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }, actions = {
                IconButton(onClick = onSearchClick) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Contact"
                    )
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings, contentDescription = "Settings"
                    )
                }
            }, scrollBehavior = scrollBehavior
            )
        }
    }
}