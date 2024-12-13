package com.githu.stephenwanjala.composecontacts.contacts.contactdetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import com.githu.stephenwanjala.composecontacts.contacts.utils.generateQRCode
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination
fun QRCodeDialog(
    navigator: DestinationsNavigator,
    contact: Contact
) {
    val qrData = """
        BEGIN:VCARD
        VERSION:3.0
        N:${contact.name}
        TEL:${contact.phoneNumbers.joinToString(";")}
        EMAIL:${contact.email ?: ""}
        END:VCARD
    """.trimIndent()

    val qrCodeBitmap = remember { generateQRCode(qrData, 512) }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(text = contact.name)
        }, navigationIcon = {
            IconButton(onClick = navigator::navigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .background(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.background
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                bitmap = qrCodeBitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Scan To quickly add Contacts", modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Thin
            )
        }
    }

}
