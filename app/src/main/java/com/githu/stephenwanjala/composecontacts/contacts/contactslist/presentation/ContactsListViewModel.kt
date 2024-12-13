package com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.data.ContactsDataSource
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsListViewModel @Inject constructor(
    private val dataSource: ContactsDataSource
) : ViewModel() {

    private val _state: MutableStateFlow<ContactsListState> = MutableStateFlow(ContactsListState())
    val state = _state
        .onStart { loadContacts() }
        .stateIn(
            scope = viewModelScope, SharingStarted.WhileSubscribed(5000),
            ContactsListState()
        )


    private fun loadContacts() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.getContacts().let { contacts ->
                _state.update { it.copy(contacts = contacts,isLoading = false) }
            }
        }
    }

}


data class ContactsListState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = ""
)