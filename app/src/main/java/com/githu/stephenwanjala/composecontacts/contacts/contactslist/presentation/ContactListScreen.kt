@file:OptIn(ExperimentalMaterial3Api::class)

package com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation.components.ContactItem
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation.components.SearchableLargeTopAppBar
import com.githu.stephenwanjala.composecontacts.contacts.destinations.AddNewContactScreenDestination
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
    val searchScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val isCollapsed = collapsedFraction > 0.5f // Threshold to switch layout state
    val numberOfContacts = remember { mutableIntStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        PermissionRequestScreen {
            val viewModel: ContactsListViewModel = hiltViewModel()
            val searchQuery = viewModel.searchQuery.collectAsStateWithLifecycle()
            val state = viewModel.state.collectAsStateWithLifecycle()
            Scaffold(
                floatingActionButton = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.animateContentSize()
                    ) {
                        FloatingActionButton(
                            onClick = {
                                navigator.navigate(AddNewContactScreenDestination)
                            },
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AddCircle,
                                contentDescription = "Add contact"
                            )
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
                    SearchableLargeTopAppBar(
                        numberOfContacts = numberOfContacts.intValue,
                        scrollBehavior = scrollBehavior,
                        searchScrollBehavior = searchScrollBehavior,
                        isCollapsed = isCollapsed,
                        searchQuery = searchQuery.value,
                        isSearchActive = state.value.isSearchActive,
                        onSearchQueryChange = { newQuery ->
                            viewModel.updateSearchQuery(newQuery)
                        },
                        onSearchClick = {
                            viewModel.updateSearchState(true)
                        },
                        onSearchDismiss = {
                            viewModel.updateSearchState(false)
                            viewModel.updateSearchQuery("")
                        },
                        onSettingsClick = {},
                    )
                }
            ) { innerPaddings ->
                numberOfContacts.intValue = state.value.contacts.size
                ContactsList(
                    modifier = Modifier
                        .padding(innerPaddings)
                        .nestedScroll(if (state.value.isSearchActive) searchScrollBehavior.nestedScrollConnection else scrollBehavior.nestedScrollConnection),
                    onClickContact = { contact ->
                        navigator.navigate(ContactDetailsScreenDestination(contact = contact))
                    },
                    onRefresh = {
                        viewModel.onAction(ContactListAction.RefreshContacts)
                    },
                    state = state.value
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
    onRefresh: () -> Unit,
    state: ContactsListState

) {
    val pullToRefreshState = rememberPullToRefreshState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        AnimatedVisibility(state.isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
            )
        }

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            state = pullToRefreshState, onRefresh = onRefresh
        ) {
            if(state.groupedContacts.isEmpty() && !state.isLoading){
                Text(text = "No Contacts Available On Device",
                    modifier = Modifier.align(Alignment.Center))
            } else{
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    state.groupedContacts.forEach { (initial, contactsForInitial) ->
                        stickyHeader {
                            CharacterHeader(initial)
                        }

                        items(contactsForInitial) { contact ->
                            ContactItem(contact = contact, onClickContact = onClickContact)
                        }
                    }
                }
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

