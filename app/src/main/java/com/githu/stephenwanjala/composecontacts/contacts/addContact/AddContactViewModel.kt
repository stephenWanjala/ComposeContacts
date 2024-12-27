package com.githu.stephenwanjala.composecontacts.contacts.addContact

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddContactViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(AddNewContactState())
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(500), _state.value)

    private val _events: Channel<AddContactEvent> = Channel()
    val events = _events.receiveAsFlow()

    fun onAction(action: AddContactAction) {
        when (action) {
            is AddContactAction.NameChanged -> {
                val isValid = action.name.isNotEmpty()
                _state.value = _state.value.copy(
                    name = action.name,
                    nameError = if (isValid) null else "Name cannot be empty"
                )
            }

            is AddContactAction.PhoneNumberChanged -> {
                val isValid = isValidPhoneNumber(action.phoneNumber)
                _state.update {
                    it.copy(
                        phoneNumber = action.phoneNumber,
                        phoneNumberError = if (isValid) null else "Invalid phone number"
                    )
                }
            }

            is AddContactAction.EmailChanged -> {
                val isValid = isValidEmail(action.email)
                _state.update {
                    it.copy(
                        email = action.email,
                        emailError = if (isValid) null else "Invalid email"
                    )
                }
            }

            is AddContactAction.SaveContact -> {
                viewModelScope.launch {
                    if (_state.value.phoneNumberError == null && _state.value.emailError == null && _state.value.nameError == null) {
                        _events.send(AddContactEvent.ContactSaved(_state.value.name, _state.value.phoneNumber, _state.value.email))
                    }
                }
            }
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {

        return phoneNumber.length >= 10 && phoneNumber.all { it.isDigit() } && Patterns.PHONE.matcher(
            phoneNumber
        ).matches()
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() or email.isEmpty()
    }

}


sealed interface AddContactAction {
    data class NameChanged(val name: String) : AddContactAction
    data class PhoneNumberChanged(val phoneNumber: String) : AddContactAction
    data class EmailChanged(val email: String) : AddContactAction
    object SaveContact : AddContactAction
}

sealed interface AddContactEvent {
    data class ContactSaved(
        val name: String,
        val phoneNumber: String,
        val email: String? = null
    ) : AddContactEvent
}


data class AddNewContactState(
    val name: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val phoneNumberError: String? = null,
    val emailError: String? = null,
    val nameError: String? = null
)