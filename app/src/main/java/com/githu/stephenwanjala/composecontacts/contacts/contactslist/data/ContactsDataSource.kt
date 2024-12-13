package com.githu.stephenwanjala.composecontacts.contacts.contactslist.data

import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactsDataSource @Inject constructor(private val contentResolver: ContentResolver) {
    suspend fun getContacts(limit: Int = 100): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )

        try {
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC "
            )?.use { cursor ->
                val contactMap = mutableMapOf<String, Contact>()

                val contactIdIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val photoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

                while (cursor.moveToNext()) {
                    // Safely retrieve values
                    val contactId = if (contactIdIndex != -1) cursor.getString(contactIdIndex) ?: continue else continue
                    val name = if (nameIndex != -1) cursor.getString(nameIndex) ?: "Unknown" else "Unknown"
                    val phoneNumber = if (numberIndex != -1) cursor.getString(numberIndex) ?: continue else continue
                    val photoUri = if (photoIndex != -1) cursor.getString(photoIndex) else null

                    val contact = contactMap.getOrPut(contactId) {
                        Contact(
                            id = contactId,
                            name = name,
                            phoneNumbers = mutableListOf(),
                            email = null,
                            photoUri = photoUri
                        )
                    }

                    (contact.phoneNumbers as MutableList).add(phoneNumber)
                }

                // Fetch emails
                for (contact in contactMap.values) {
                    val emailCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS),
                        "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?",
                        arrayOf(contact.id),
                        null
                    )

                    emailCursor?.use { ec ->
                        val emailIndex = ec.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
                        if (emailIndex != -1 && ec.moveToFirst()) {
                            contact.email = ec.getString(emailIndex)
                        }
                    }
                }

                contacts.addAll(contactMap.values)
            }
        } catch (e: Exception) {
            Log.e("ContactsDataSource", "Error fetching contacts", e)
        }

        contacts
    }
}