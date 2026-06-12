package com.githu.stephenwanjala.composecontacts.contacts.addContact

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddContactViewModel : ViewModel() {

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

            is AddContactAction.SaveContact -> saveContact()
        }
    }

    private fun saveContact() {
        // Re-validate every field on save so an untouched (still-null) error
        // can never let an empty/invalid contact through.
        val current = _state.value
        val nameError = if (current.name.isNotBlank()) null else "Name cannot be empty"
        val phoneError = if (isValidPhoneNumber(current.phoneNumber)) null else "Invalid phone number"
        val emailError = if (isValidEmail(current.email)) null else "Invalid email"

        _state.update {
            it.copy(nameError = nameError, phoneNumberError = phoneError, emailError = emailError)
        }

        if (nameError != null || phoneError != null || emailError != null) return

        viewModelScope.launch {
            _events.send(
                AddContactEvent.ContactSaved(
                    name = current.name.trim(),
                    phoneNumber = current.phoneNumber.trim(),
                    email = current.email.trim().ifBlank { null }
                )
            )
        }
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val trimmed = phoneNumber.trim()
        // Allow common dialable characters (+, spaces, dashes, parentheses) but
        // require at least 7 actual digits, which covers international formats.
        val digitCount = trimmed.count { it.isDigit() }
        return digitCount >= 7 && Patterns.PHONE.matcher(trimmed).matches()
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isBlank() || Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
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