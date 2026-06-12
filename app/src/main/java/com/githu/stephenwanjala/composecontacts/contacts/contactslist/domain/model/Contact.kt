package com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Contact(
    val id: String,
    val name: String,
    val phoneNumbers: List<String>,
    var email: String?,
    var photoUri: String?
) : Parcelable {
    /**
     * The grouping/avatar initial for this contact. Falls back to '#' for blank
     * or non-letter leading characters so empty names never crash on `.first()`.
     */
    val initial: Char
        get() = name.trim().firstOrNull()?.takeIf { it.isLetter() }?.uppercaseChar() ?: '#'
}
