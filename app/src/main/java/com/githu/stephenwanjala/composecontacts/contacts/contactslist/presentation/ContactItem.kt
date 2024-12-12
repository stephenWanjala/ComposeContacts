package com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import com.githu.stephenwanjala.composecontacts.ui.theme.ComposeContactsTheme

@Composable
fun ContactItem(
    contact: Contact,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Contact Avatar
        Surface(
            modifier = Modifier.size(40.dp),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = contact.name.first().toString(),
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Contact Details
        Column {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyLarge
            )
            if (contact.phoneNumber.isNotEmpty()) {
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Preview(showBackground = true,)
@PreviewLightDark
@Composable
private fun ContactItemPreview() {
    ComposeContactsTheme {
        ContactItem(
            contact = Contact(
                id = "1",
                phoneNumber = "+254723441943",
                email = "stephenwanjala145@gmail.com",
                name = "Wanjala Stephen",
                photoUri = ""
            )
        )
    }
}