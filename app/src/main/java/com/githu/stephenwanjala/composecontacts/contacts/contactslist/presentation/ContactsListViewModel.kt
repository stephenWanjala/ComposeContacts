package com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.data.ContactsDataSource
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsListViewModel @Inject constructor(
    private val dataSource: ContactsDataSource
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _state: MutableStateFlow<ContactsListState> = MutableStateFlow(ContactsListState())
    val state = combine(_state, _searchQuery) { state, query ->
        if (query.isEmpty()) {
            state
        } else {
            state.copy(
                groupedContacts = state.contacts
                    .filter { contact ->
                        contact.name.contains(query, ignoreCase = true) ||
                                contact.phoneNumbers.any { it.contains(query) } ||
                                contact.email?.contains(query, ignoreCase = true) == true
                    }
                    .groupBy { it.name.first().uppercaseChar() }
            )
        }
    }
        .onStart { loadContacts() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ContactsListState()
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun loadContacts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val contacts = dataSource.getContacts()
                _state.update {
                    it.copy(
                        contacts = contacts,
                        isLoading = false,
                        groupedContacts = contacts.groupBy { it.name.first().uppercaseChar() }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load contacts: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun updateSearchState(bool: Boolean) {
        _state.update { it.copy(isSearchActive = bool) }
    }
}

data class ContactsListState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = "",
    val groupedContacts: Map<Char, List<Contact>> = emptyMap(),
    val isSearchActive: Boolean = false
)
