package com.githu.stephenwanjala.composecontacts.contacts.utils

import android.database.Cursor

fun Cursor.getStringOrNull(columnIndex: Int): String? {
    return if (isNull(columnIndex)) null else getString(columnIndex)
}

fun Cursor.getStringOrDefault(columnIndex: Int, default: String): String {
    return getStringOrNull(columnIndex) ?: default
}