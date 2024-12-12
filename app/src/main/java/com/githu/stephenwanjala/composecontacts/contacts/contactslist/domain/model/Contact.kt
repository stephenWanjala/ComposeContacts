package com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String?,
    val photoUri: String?
)
