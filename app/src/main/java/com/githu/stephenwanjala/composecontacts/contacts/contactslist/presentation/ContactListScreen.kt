package com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation.components.ContactItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph


@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun ContactListScreen() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val isCollapsed = collapsedFraction > 0.5f // Threshold to switch layout state

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            floatingActionButton = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = {},
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Add contact")
                    }

                    FloatingActionButton(
                        onClick = {},
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = "Share contact"
                        )
                    }
                }
            },
            topBar = {
                LargeTopAppBar(
                    title = {
                        Column(
                            modifier = Modifier
                                .animateContentSize()
                        ) {
                            if (isCollapsed) {

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Contacts",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        text = "350 Contacts",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            } else {
                                Column {
                                    Text(
                                        text = "Contacts",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = "350 Contacts",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Contact"
                            )
                        }

                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    },
                )
            }
        ) { innerPaddings ->
            val contacts = remember {
                List(50) { index ->
                    Contact(
                        id = index.toString(),
                        name = "Contact $index",
                        phoneNumber = "+1 555-${index.toString().padStart(4, '0')}",
                        email = "test${index + 1}@gmail.com",
                        photoUri = null
                    )
                }
            }
            ContactsList(
                contacts = contacts,
                modifier = Modifier
                    .padding(innerPaddings)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            )
        }
    }
}



@Composable
fun ContactsList(
    contacts: List<Contact>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(contacts) { contact ->
            ContactItem(contact = contact)
        }
    }
}

