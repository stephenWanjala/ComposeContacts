package com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.data.ContactsDataSource
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.data.search.RecentSearchesRepository
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class ContactsListViewModel(
    private val dataSource: ContactsDataSource,
    private val recentSearches: RecentSearchesRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Bumped whenever the search history changes so the suggestions flow re-reads
    // it even when the query text itself hasn't changed.
    private val _suggestionsRefresh = MutableStateFlow(0)

    /** Recent searches from [RecentSearchesRepository], filtered by the live query. */
    val recentSuggestions: StateFlow<List<String>> =
        combine(_searchQuery.debounce(120.milliseconds), _suggestionsRefresh) { query, _ -> query }
            .mapLatest { query -> recentSearches.getSuggestions(query) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    private val _state = MutableStateFlow(ContactsListState(isLoading = true))
    val state = combine(_state, _searchQuery) { state, query ->
        val trimmedQuery = query.trim()
        if (trimmedQuery.isEmpty()) {
            state
        } else {
            state.copy(
                groupedContacts = state.contacts
                    .filter { contact ->
                        contact.name.contains(trimmedQuery, ignoreCase = true) ||
                                contact.phoneNumbers.any { it.contains(trimmedQuery) } ||
                                contact.email?.contains(trimmedQuery, ignoreCase = true) == true
                    }
                    .groupBy { it.initial }
            )
        }
    }
        .onStart { loadContacts() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ContactsListState(isLoading = true)
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /** Commit a query to the recent-search history (keyboard submit or result tap). */
    fun onSearchSubmit(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return
        _searchQuery.value = trimmed
        recentSearches.saveQuery(trimmed)
        _suggestionsRefresh.update { it + 1 }
    }

    fun clearSearchHistory() {
        recentSearches.clearHistory()
        _suggestionsRefresh.update { it + 1 }
    }

    fun onAction(action: ContactListAction) {
        when (action) {
            ContactListAction.RefreshContacts -> refreshContacts()
        }
    }

    private fun refreshContacts() = updateContacts(isRefreshing = true)

    private fun loadContacts() = updateContacts()

    private fun updateContacts(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = !isRefreshing, isRefreshing = isRefreshing) }
            runCatching { dataSource.getContacts() }
                .onSuccess { contacts ->
                    _state.update {
                        it.copy(
                            contacts = contacts,
                            isLoading = false,
                            groupedContacts = contacts.groupBy { it.initial },
                            isRefreshing = false,
                            error = ""
                        )
                    }
                }
                .onFailure { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = "Failed to load contacts: ${exception.localizedMessage}"
                        )
                    }
                }
        }
    }

    fun updateSearchState(isActive: Boolean) {
        _state.update { it.copy(isSearchActive = isActive) }
    }
}

data class ContactsListState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = "",
    val groupedContacts: Map<Char, List<Contact>> = emptyMap(),
    val isSearchActive: Boolean = false,
    val isRefreshing: Boolean = false
)

sealed interface ContactListAction {
    data object RefreshContacts : ContactListAction
}
