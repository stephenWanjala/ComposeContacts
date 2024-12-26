package com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation.components.ContactItem
import com.githu.stephenwanjala.composecontacts.contacts.destinations.ContactDetailsScreenDestination
import com.githu.stephenwanjala.composecontacts.core.presenation.components.PermissionRequestScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun ContactListScreen(navigator: DestinationsNavigator) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val isCollapsed = collapsedFraction > 0.5f // Threshold to switch layout state
    val numberOfContacts = remember { mutableIntStateOf(0) }
    remember { mutableStateOf(false) }

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
                        Icon(imageVector = Icons.Outlined.AddCircle, contentDescription = "Add contact")
                    }
//                  TODO()  share selected contact
                    AnimatedVisibility(false) {
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
                                        text = "${numberOfContacts.intValue} Contacts",
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
                                        text = "${numberOfContacts.intValue} Contacts",
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

            PermissionRequestScreen {
                val viewModel: ContactsListViewModel = hiltViewModel()
                val state = viewModel.state.collectAsStateWithLifecycle()
                numberOfContacts.intValue = state.value.contacts.size
                AnimatedVisibility(state.value.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPaddings),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                ContactsList(
                    groupedContacts = state.value.groupedContacts,
                    modifier = Modifier
                        .padding(innerPaddings)
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    onClickContact = {contact->
                        navigator.navigate(ContactDetailsScreenDestination(contact = contact))
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactsList(
    modifier: Modifier = Modifier,
    onClickContact: (Contact) -> Unit,
    groupedContacts: Map<Char, List<Contact>>


) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        groupedContacts.forEach { (initial, contactsForInitial) ->
            stickyHeader {
                CharacterHeader(initial)
            }

            items(contactsForInitial) { contact ->
                ContactItem (contact=contact, onClickContact = onClickContact)
            }
        }
    }
}

@Composable
fun CharacterHeader(characterData: Char) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = characterData.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

