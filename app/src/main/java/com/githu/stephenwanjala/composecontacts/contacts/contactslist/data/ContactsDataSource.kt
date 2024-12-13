package com.githu.stephenwanjala.composecontacts.contacts.contactslist.data

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import javax.inject.Inject

class ContactsDataSource @Inject constructor(private val contentResolver: ContentResolver) {
    fun getContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone._ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )

        try {
            val cursor: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )

            cursor?.use { c ->
                val idIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val photoIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

                // This map will store contacts by their ID to collect multiple phone numbers
                val contactMap = mutableMapOf<String, Contact>()

                while (c.moveToNext()) {
                    val contactId = c.getString(idIndex)
                    val contactName = c.getString(nameIndex) ?: "Unknown"
                    val contactPhoneNumber = c.getString(numberIndex) ?: ""
                    val contactPhotoUri = c.getString(photoIndex)

                    // Get the contact from the map, or create a new one if not found
                    val contact = contactMap.getOrPut(contactId) {
                        Contact(
                            id = contactId,
                            name = contactName,
                            phoneNumbers = mutableListOf(),
                            email = null,
                            photoUri = contactPhotoUri
                        )
                    }

                    // Add the phone number to the contact's list of phone numbers
                    (contact.phoneNumbers as MutableList).add(contactPhoneNumber)
                }

                // Now handle email fetching
                for (contact in contactMap.values) {
                    var contactEmail: String? = null
                    val emailCursor: Cursor? = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        arrayOf(contact.id),
                        null
                    )

                    emailCursor?.use { ec ->
                        if (ec.moveToFirst()) {
                            contactEmail = ec.getString(ec.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS))
                        }
                    }

                    // Update the contact with the email
                    val updatedContact = contact.copy(email = contactEmail)
                    contacts.add(updatedContact)
                }
            }
        } catch (e: Exception) {
            Log.e("ContactsHelper", "Error fetching contacts", e)
        }

        return contacts
    }
}
