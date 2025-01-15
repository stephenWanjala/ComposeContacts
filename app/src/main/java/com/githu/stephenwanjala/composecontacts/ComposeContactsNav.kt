package com.githu.stephenwanjala.composecontacts

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.githu.stephenwanjala.composecontacts.contacts.addContact.AddNewContactScreen
import com.githu.stephenwanjala.composecontacts.contacts.contactdetails.ContactDetailsScreen
import com.githu.stephenwanjala.composecontacts.contacts.contactdetails.QRCodeDialog
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation.ContactListScreen
import kotlinx.serialization.Serializable


@Composable
fun ComposeContactsNav(
    navHostController: NavHostController,
    modifier: Modifier = Modifier) {
    NavHost(navController = navHostController, startDestination = Screen.ContactListDestination,
        modifier = modifier) {
        composable<Screen.ContactListDestination> {
            ContactListScreen(navHostController = navHostController)
        }
        composable<Screen.AddNewContactDestination> {
            AddNewContactScreen(navigator = navHostController)
        }
        composable<Screen.ContactDetailsDestination> {
            val route = it.toRoute<Screen.ContactDetailsDestination>()
            val contact = Contact(
                id = route.id,
                email = route.email,
                phoneNumbers = route.phoneNumbers,
                photoUri = route.photoUri,
                name = route.name
            )
            ContactDetailsScreen(contact = contact, navController = navHostController)
        }
        composable<Screen.QRCodeDialogDestination> {
            val route = it.toRoute<Screen.QRCodeDialogDestination>()
            val contact = Contact(
                id = route.id,
                email = route.email,
                phoneNumbers = route.phoneNumbers,
                photoUri = route.photoUri,
                name = route.name
            )
            QRCodeDialog(navController = navHostController, contact = contact)
        }
    }

}

@Serializable
sealed interface Screen{
    @Serializable
    data object ContactListDestination:Screen
    @Serializable
    data object AddNewContactDestination:Screen
    @Serializable
    data class ContactDetailsDestination(
        val id: String,
        val name: String,
        val phoneNumbers: List<String>,
        var email: String?,
        var photoUri: String?
    ):Screen
    @Serializable
    data class QRCodeDialogDestination(
        val id: String,
        val name: String,
        val phoneNumbers: List<String>,
        var email: String?,
        var photoUri: String?
    ):Screen

}