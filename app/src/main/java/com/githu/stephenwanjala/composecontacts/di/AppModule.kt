package com.githu.stephenwanjala.composecontacts.di

import android.app.Application
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.data.ContactsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideContactsDatasource(app: Application): ContactsDataSource {
        return ContactsDataSource(
            app.contentResolver
        )
    }
}