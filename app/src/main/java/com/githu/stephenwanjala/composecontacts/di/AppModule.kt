package com.githu.stephenwanjala.composecontacts.di
import com.githu.stephenwanjala.composecontacts.contacts.addContact.AddContactViewModel
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.data.ContactsDataSource
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation.ContactsListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        ContactsDataSource(androidApplication().contentResolver)
    }

    viewModelOf(::ContactsListViewModel)
    viewModelOf(::AddContactViewModel)
}