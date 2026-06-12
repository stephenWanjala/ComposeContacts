package com.githu.stephenwanjala.composecontacts.contacts
import android.app.Application
import com.githu.stephenwanjala.composecontacts.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ContactsApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@ContactsApp)
            modules(appModule)
        }
    }
}