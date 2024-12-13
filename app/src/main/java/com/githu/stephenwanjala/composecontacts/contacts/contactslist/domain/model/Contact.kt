package com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: String,
    val name: String,
    val phoneNumbers: List<String>,
    var email: String?,
    var photoUri: String?
) : Parcelable
