package com.githu.stephenwanjala.composecontacts.contacts.contactdetails

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import com.githu.stephenwanjala.composecontacts.contacts.utils.generateQRCode

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun QRCodeDialog(
    navController: NavHostController,
    contact: Contact,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val qrData = remember(contact) {
        buildString {
            appendLine("BEGIN:VCARD")
            appendLine("VERSION:3.0")
            appendLine("N:${contact.name}")
            appendLine("FN:${contact.name}")
            contact.phoneNumbers.forEach { appendLine("TEL:$it") }
            contact.email?.takeIf { it.isNotBlank() }?.let { appendLine("EMAIL:$it") }
            append("END:VCARD")
        }
    }

    val qrCodeBitmap = remember(qrData) { generateQRCode(qrData, 512) }

    Scaffold(topBar = {
        TopAppBar(title = {
            with(sharedTransitionScope) {
                Text(
                    text = contact.name,
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(key = "contact-name-${contact.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                )
            }
        }, navigationIcon = {
            IconButton(onClick = navController::navigateUp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = Color.White,
                tonalElevation = 4.dp,
                shadowElevation = 4.dp
            ) {
                Image(
                    bitmap = qrCodeBitmap.asImageBitmap(),
                    contentDescription = "QR Code for ${contact.name}",
                    modifier = Modifier
                        .padding(24.dp)
                        .size(260.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Scan to quickly add this contact",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}