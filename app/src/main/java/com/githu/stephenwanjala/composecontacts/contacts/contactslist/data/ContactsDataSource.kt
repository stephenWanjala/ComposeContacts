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
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
            ContactsContract.CommonDataKinds.Email.ADDRESS
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
                val nameIndex =
                    c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val photoIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)
                val emailIndex = c.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)

                while (c.moveToNext()) {
                    val contact = Contact(
                        id = c.getString(idIndex),
                        name = c.getString(nameIndex) ?: "Unknown",
                        phoneNumber = c.getString(numberIndex) ?: "",
                        photoUri = c.getString(photoIndex),
                        email = c.getString(emailIndex)
                    )
                    contacts.add(contact)
                }
            }
        } catch (e: Exception) {
            Log.e("ContactsHelper", "Error fetching contacts", e)
        }

        return contacts
    }

}