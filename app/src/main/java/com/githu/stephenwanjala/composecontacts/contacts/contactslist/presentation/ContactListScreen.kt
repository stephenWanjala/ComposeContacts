@file:OptIn(ExperimentalMaterial3Api::class)

package com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.githu.stephenwanjala.composecontacts.Screen
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation.components.ContactItem
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation.components.SearchableLargeTopAppBar
import com.githu.stephenwanjala.composecontacts.core.presenation.components.PermissionRequestScreen
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun ContactListScreen(
    navHostController: NavHostController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val searchScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val collapsedFraction = scrollBehavior.state.collapsedFraction
    val isCollapsed = collapsedFraction > 0.5f // Threshold to switch layout state

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        PermissionRequestScreen {
            val viewModel: ContactsListViewModel = koinViewModel()
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
                                navHostController.navigate(Screen.AddNewContactDestination)
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
                        numberOfContacts = state.value.contacts.size,
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
                ContactsList(
                    modifier = Modifier
                        .padding(innerPaddings)
                        .consumeWindowInsets(innerPaddings)
                        .nestedScroll(if (state.value.isSearchActive) searchScrollBehavior.nestedScrollConnection else scrollBehavior.nestedScrollConnection),
                    onClickContact = { contact ->
                        navHostController.navigate(
                            Screen.ContactDetailsDestination(
                                id = contact.id,
                                name = contact.name,
                                phoneNumbers = contact.phoneNumbers,
                                photoUri = contact.photoUri,
                                email = contact.email
                            )
                        )
                    },
                    onRefresh = {
                        viewModel.onAction(ContactListAction.RefreshContacts)
                    },
                    state = state.value,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )
            }
        }

    }
}


@ExperimentalSharedTransitionApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactsList(
    modifier: Modifier = Modifier,
    onClickContact: (Contact) -> Unit,
    onRefresh: () -> Unit,
    state: ContactsListState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope

) {
    val pullToRefreshState = rememberPullToRefreshState()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
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
            if (state.groupedContacts.isEmpty() && !state.isLoading) {
                EmptyContactsMessage(
                    isSearching = state.isSearchActive,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    state.groupedContacts.forEach { (initial, contactsForInitial) ->
                        stickyHeader {
                            CharacterHeader(initial)
                        }

                        items(contactsForInitial, key = { it.id }) { contact ->
                            ContactItem(contact = contact, onClickContact = onClickContact,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope)
                        }
                    }
                }
            }
        }
    }


}

@Composable
fun EmptyContactsMessage(
    isSearching: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = if (isSearching) Icons.Outlined.SearchOff else Icons.Outlined.Contacts,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = if (isSearching) "No matching contacts" else "No contacts yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = if (isSearching)
                "Try a different name, number or email."
            else
                "Tap the + button to add your first contact.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CharacterHeader(characterData: Char) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Text(
            text = characterData.toString(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

