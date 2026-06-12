@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.githu.stephenwanjala.composecontacts.contacts.contactdetails

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalPhone
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.githu.stephenwanjala.composecontacts.Screen
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(
    navController: NavHostController,
    contact: Contact,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val primaryPhone = contact.phoneNumbers.firstOrNull()

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = navController::navigateUp) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.StarBorder,
                                contentDescription = "Star Contact",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Contact")
                        }
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options"
                            )
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileHeader(
                    contact = contact,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope
                )

                QuickActionsRow(
                    hasPhone = primaryPhone != null,
                    hasEmail = contact.email != null,
                    onCall = { primaryPhone?.let { context.dial(it) } },
                    onMessage = { primaryPhone?.let { context.sms(it) } },
                    onEmail = { contact.email?.let { context.email(it) } },
                    onQrCode = {
                        navController.navigate(
                            Screen.QRCodeDialogDestination(
                                id = contact.id,
                                email = contact.email,
                                phoneNumbers = contact.phoneNumbers,
                                photoUri = contact.photoUri,
                                name = contact.name
                            )
                        )
                    }
                )

                ContactDetailsSection(contact = contact)
            }
        }
    }
}

@Composable
fun ProfileHeader(
    contact: Contact,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {

    with(sharedTransitionScope) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            if (contact.photoUri != null) {
                AsyncImage(
                    model = contact.photoUri,
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .sharedElement(
                            rememberSharedContentState(key = "contact-photo-${contact.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    contentScale = ContentScale.Crop,
                    contentDescription = "${contact.name} Contact Image"
                )
            } else {
                Surface(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .sharedElement(
                            rememberSharedContentState(key = "contact-photo-${contact.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        ),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (contact.name.isNotBlank()) {
                            Text(
                                text = contact.initial.toString(),
                                style = MaterialTheme.typography.displaySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = "Contact Image",
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = contact.name.ifBlank { "Unknown" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .sharedBounds(
                        rememberSharedContentState(key = "contact-name-${contact.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )
        }
    }
}

@Composable
private fun QuickActionsRow(
    hasPhone: Boolean,
    hasEmail: Boolean,
    onCall: () -> Unit,
    onMessage: () -> Unit,
    onEmail: () -> Unit,
    onQrCode: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        if (hasPhone) {
            QuickAction(icon = Icons.Default.Phone, label = "Call", onClick = onCall)
            QuickAction(icon = Icons.AutoMirrored.Filled.Message, label = "Message", onClick = onMessage)
        }
        if (hasEmail) {
            QuickAction(icon = Icons.Outlined.Email, label = "Email", onClick = onEmail)
        }
        QuickAction(icon = Icons.Default.QrCode2, label = "QR", onClick = onQrCode)
    }
}

@Composable
private fun QuickAction(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalIconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ContactDetailsSection(contact: Contact) {
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        val phoneNumbers = contact.phoneNumbers.toSet()
        if (phoneNumbers.isNotEmpty()) {
            SectionLabel(text = "Phone")
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    phoneNumbers.forEach { phoneNumber ->
                        DetailRow(
                            icon = Icons.Default.LocalPhone,
                            primary = phoneNumber,
                            secondary = "Mobile",
                            onClick = { context.dial(phoneNumber) }
                        )
                    }
                }
            }
        }

        contact.email?.takeIf { it.isNotBlank() }?.let { email ->
            SectionLabel(text = "Email")
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                DetailRow(
                    icon = Icons.Outlined.Email,
                    primary = email,
                    secondary = "Email",
                    onClick = { context.email(email) }
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    primary: String,
    secondary: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = secondary,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = primary,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = secondary,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun Context.dial(phoneNumber: String) =
    launchSafely(Intent(Intent.ACTION_DIAL, "tel:$phoneNumber".toUri()), "No dialer app found")

private fun Context.sms(phoneNumber: String) =
    launchSafely(Intent(Intent.ACTION_SENDTO, "smsto:$phoneNumber".toUri()), "No messaging app found")

private fun Context.email(address: String) =
    launchSafely(Intent(Intent.ACTION_SENDTO, "mailto:$address".toUri()), "No email app found")

private fun Context.launchSafely(intent: Intent, errorMessage: String) {
    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
}
