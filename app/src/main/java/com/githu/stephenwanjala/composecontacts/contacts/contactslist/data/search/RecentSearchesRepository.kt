package com.githu.stephenwanjala.composecontacts.contacts.contactslist.data.search

import android.app.SearchManager
import android.content.Context
import android.net.Uri
import android.provider.SearchRecentSuggestions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

/**
 * Thin wrapper over the [SearchRecentSuggestions] framework helper and the
 * [ContactsSuggestionProvider] it writes to. Lets the app save submitted search
 * queries and read them back as recent suggestions.
 */
class RecentSearchesRepository(private val context: Context) {

    private val recentSuggestions = SearchRecentSuggestions(
        context,
        ContactsSuggestionProvider.AUTHORITY,
        ContactsSuggestionProvider.MODE
    )

    /** Persist a submitted query. Blank queries are ignored. */
    fun saveQuery(query: String) {
        val trimmed = query.trim()
        if (trimmed.isNotEmpty()) {
            recentSuggestions.saveRecentQuery(trimmed, null)
        }
    }

    /** Wipe the entire search history. */
    fun clearHistory() = recentSuggestions.clearHistory()

    /**
     * Read recent suggestions, most-recent first. When [query] is blank every
     * stored query is returned; otherwise the provider filters with a LIKE match.
     */
    suspend fun getSuggestions(query: String, limit: Int = 10): List<String> =
        withContext(Dispatchers.IO) {
            // The suggestions provider expects the typed text in selectionArgs[0]
            // on the standard search-suggestion URI path.
            val uri =
                "content://${ContactsSuggestionProvider.AUTHORITY}/${SearchManager.SUGGEST_URI_PATH_QUERY}".toUri()
            val results = LinkedHashSet<String>()
            runCatching {
                context.contentResolver.query(
                    uri,
                    null,
                    null,
                    arrayOf(query.trim()),
                    null
                )?.use { cursor ->
                    val textIndex = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)
                    if (textIndex >= 0) {
                        while (cursor.moveToNext() && results.size < limit) {
                            cursor.getString(textIndex)?.let(results::add)
                        }
                    }
                }
            }
            results.toList()
        }
}
