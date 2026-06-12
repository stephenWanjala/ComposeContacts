@file:OptIn(ExperimentalMaterial3Api::class)

package com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.CallMade
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact

/**
 * Material3 [SearchBar] for the contacts list. While expanded it shows recent
 * searches (when the query is blank) or live contact matches (while typing).
 *
 * It is only ever rendered in the expanded state — the caller mounts it when the
 * user taps search and removes it on dismiss — so the collapsed pill is unused.
 */
@Composable
fun ContactsSearchBar(
    query: String,
    results: List<Contact>,
    recentSuggestions: List<String>,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onResultClick: (Contact) -> Unit,
    onClearHistory: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    SearchBar(
        modifier = modifier.fillMaxWidth(),
        expanded = true,
        onExpandedChange = { expanded -> if (!expanded) onDismiss() },
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.focusRequester(focusRequester),
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = true,
                onExpandedChange = { expanded -> if (!expanded) onDismiss() },
                placeholder = { Text("Search name, number or email") },
                leadingIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Close search"
                        )
                    }
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = "Clear query"
                            )
                        }
                    }
                }
            )
        }
    ) {
        if (query.isBlank()) {
            RecentSearches(
                suggestions = recentSuggestions,
                onSuggestionClick = onSuggestionClick,
                onClearHistory = onClearHistory
            )
        } else {
            SearchResults(
                results = results,
                onResultClick = onResultClick
            )
        }
    }
}

@Composable
private fun RecentSearches(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    onClearHistory: () -> Unit,
) {
    if (suggestions.isEmpty()) {
        EmptySearchHint(text = "Search your contacts by name, number or email.")
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent searches",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onClearHistory) { Text("Clear") }
            }
        }
        items(suggestions, key = { it }) { suggestion ->
            ListItem(
                modifier = Modifier.clickable { onSuggestionClick(suggestion) },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null
                    )
                },
                headlineContent = {
                    Text(
                        text = suggestion,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                trailingContent = {
                    IconButton(onClick = { onSuggestionClick(suggestion) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.CallMade,
                            contentDescription = "Use suggestion"
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun SearchResults(
    results: List<Contact>,
    onResultClick: (Contact) -> Unit,
) {
    if (results.isEmpty()) {
        EmptySearchHint(
            icon = true,
            text = "No matching contacts."
        )
        return
    }
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(results, key = { it.id }) { contact ->
            ListItem(
                modifier = Modifier.clickable { onResultClick(contact) },
                leadingContent = {
                    ContactAvatar(contact = contact, modifier = Modifier.size(40.dp))
                },
                headlineContent = {
                    Text(
                        text = contact.name.ifBlank { "Unknown" },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingContent = {
                    contact.phoneNumbers.firstOrNull()?.let { phone ->
                        Text(
                            text = phone,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptySearchHint(
    text: String,
    icon: Boolean = false,
) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (icon) {
            Icon(
                imageVector = Icons.Outlined.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
