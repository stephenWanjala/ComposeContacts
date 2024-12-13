package com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model

data class Contact(
    val id: String,
    val name: String,
    val phoneNumbers: List<String>,
    var email: String?,
    var photoUri: String?
)
