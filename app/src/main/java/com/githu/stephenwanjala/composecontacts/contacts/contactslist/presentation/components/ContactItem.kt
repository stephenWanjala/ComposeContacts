package com.githu.stephenwanjala.composecontacts.contacts.contactslist.presentation.components

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.githu.stephenwanjala.composecontacts.contacts.contactslist.domain.model.Contact
import com.githu.stephenwanjala.composecontacts.ui.theme.ComposeContactsTheme

@Composable
fun ContactItem(
    contact: Contact,
    modifier: Modifier = Modifier,
    onClickContact: (Contact) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { onClickContact(contact) })
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary
        ) {
            if (contact.photoUri != null) {
                AsyncImage(
                    model = contact.photoUri,
                    contentDescription = null,
                )
            } else {
                Text(
                    text = contact.name.first().toString().uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.bodyLarge
            )
            if (contact.phoneNumbers.isNotEmpty()) {
                BlurredPhoneNumber(contact)
            }
        }
    }
}

@Composable
fun BlurredPhoneNumber(contact: Contact) {
    val phoneNumber = contact.phoneNumbers.firstOrNull() ?: "Unknown" // Handle null or empty list gracefully
    val blurPaintColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f).toArgb()

    if (phoneNumber.length > 4) {
        val prefix = phoneNumber.substring(0, 3)
        val suffix = phoneNumber.substring(phoneNumber.length - 1)
        val numberToBlur = phoneNumber.substring(3, phoneNumber.length - 1) // Range to blur

        Row {
            // Prefix (first 3 characters)
            Text(
                text = prefix,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            // Blurred middle digits
            Canvas(modifier = Modifier.height(20.dp)) {
                val blurPaint = Paint().asFrameworkPaint().apply {
                    textSize = 16.sp.toPx()
                    color = blurPaintColor
                }

                // Reduce blur radius progressively
                numberToBlur.forEachIndexed { index, char ->
                    val radius = (10f - (index * 5)).coerceAtLeast(6f) // Clamp radius to a minimum of 5f
                    blurPaint.maskFilter = BlurMaskFilter(radius, BlurMaskFilter.Blur.NORMAL)
                    drawContext.canvas.nativeCanvas.drawText(
                        char.toString(),
                        index * 10f, // Adjust spacing between characters
                        16.sp.toPx(),
                        blurPaint
                    )
                }
            }

            // Suffix (last character)
            Text(
                text = suffix,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    } else {
        // If the phone number is too short, blur the entire number
        Canvas(modifier = Modifier.height(20.dp)) {

            val blurPaint = Paint().asFrameworkPaint().apply {
                textSize = 16.sp.toPx()
                color = blurPaintColor
                maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL)
            }
            drawContext.canvas.nativeCanvas.drawText(
                phoneNumber,
                0f,
                16.sp.toPx(),
                blurPaint
            )
        }
    }
}






@Preview(showBackground = true)
@PreviewLightDark
@Composable
private fun ContactItemPreview() {
    ComposeContactsTheme {
        ContactItem(
            contact = Contact(
                id = "1",
                phoneNumbers = listOf("+254723441943"),
                email = "stephenwanjala145@gmail.com",
                name = "Wanjala Stephen",
                photoUri = ""
            ),
            onClickContact = { },
        )
    }
}