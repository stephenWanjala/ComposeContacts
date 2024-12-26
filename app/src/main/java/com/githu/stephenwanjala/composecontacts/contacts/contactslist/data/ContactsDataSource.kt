package com.githu.stephenwanjala.composecontacts.contacts.contactslist.data

import android.content.ContentResolver
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import android.util.Log
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactsDataSource @Inject constructor(private val contentResolver: ContentResolver) {
    suspend fun getContacts(): List<Contact> = withContext(Dispatchers.IO) {
        val contacts = mutableListOf<Contact>()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI
        )

        try {
            // Fetch contacts (phone numbers) in one query
            contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
            )?.use { cursor ->
                val contactMap = mutableMapOf<String, Contact>()

                val contactIdIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val photoIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)

                while (cursor.moveToNext()) {
                    val contactId = cursor.getString(contactIdIndex) ?: continue
                    val name = cursor.getString(nameIndex) ?: "Unknown"
                    val rawPhoneNumber = cursor.getString(numberIndex) ?: continue
                    val normalizedPhoneNumber = PhoneNumberUtils.normalizeNumber(rawPhoneNumber)
                    val photoUri = cursor.getString(photoIndex)

                    val contact = contactMap.getOrPut(contactId) {
                        Contact(
                            id = contactId,
                            name = name,
                            phoneNumbers = mutableListOf(),
                            email = null,
                            photoUri = photoUri
                        )
                    }

//                    (contact.phoneNumbers as MutableList).add(normalizedPhoneNumber)
                    if (!contact.phoneNumbers.contains(normalizedPhoneNumber)) {
                        (contact.phoneNumbers as MutableList).add(normalizedPhoneNumber)
                    }
                }

                // Fetch emails for all contacts in a single batch query
                val contactIds = contactMap.keys.joinToString(",")
                if (contactIds.isNotEmpty()) {
                    val emailCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        arrayOf(
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Email.ADDRESS
                        ),
                        "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} IN ($contactIds)",
                        null,
                        null
                    )

                    emailCursor?.use { ec ->
                        val contactIdIndex = ec.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
                        val emailIndex = ec.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)

                        while (ec.moveToNext()) {
                            val contactId = ec.getString(contactIdIndex) ?: continue
                            val email = ec.getString(emailIndex)
                            contactMap[contactId]?.email = email
                        }
                    }
                }

                // Add contacts to the final list
                contacts.addAll(contactMap.values)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ContactsDataSource", "Error fetching contacts", e)
        }

        contacts
    }
}
