package com.githu.stephenwanjala.composecontacts.contacts.contactslist.data

import android.content.ContentResolver
import android.provider.ContactsContract
import android.telephony.PhoneNumberUtils
import android.util.Log
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsDataSource(private val contentResolver: ContentResolver) {
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
                // Use ordered sets for phone numbers so dedup is O(1) instead of
                // an O(n) contains() scan per row.
                val numbersByContact = LinkedHashMap<String, LinkedHashSet<String>>()
                val contactMap = LinkedHashMap<String, Contact>()

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

                    val numbers = numbersByContact.getOrPut(contactId) {
                        contactMap[contactId] = Contact(
                            id = contactId,
                            name = name,
                            phoneNumbers = mutableListOf(),
                            email = null,
                            photoUri = photoUri
                        )
                        LinkedHashSet()
                    }
                    numbers.add(normalizedPhoneNumber)
                }

                // Fetch every email in one flat table scan and join in memory.
                // This avoids building a huge "CONTACT_ID IN (...)" clause, which
                // is slow to plan and can hit SQLite's expression limit.
                contentResolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Email.ADDRESS
                    ),
                    null,
                    null,
                    null
                )?.use { ec ->
                    val emailContactIdIndex = ec.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID)
                    val emailIndex = ec.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)

                    while (ec.moveToNext()) {
                        val contactId = ec.getString(emailContactIdIndex) ?: continue
                        val contact = contactMap[contactId] ?: continue
                        if (contact.email == null) {
                            contact.email = ec.getString(emailIndex)
                        }
                    }
                }

                // Materialize the phone numbers onto each contact.
                contactMap.forEach { (id, contact) ->
                    (contact.phoneNumbers as MutableList).addAll(numbersByContact[id].orEmpty())
                }
                contacts.addAll(contactMap.values)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("ContactsDataSource", "Error fetching contacts", e)
        }

        contacts
    }
}
