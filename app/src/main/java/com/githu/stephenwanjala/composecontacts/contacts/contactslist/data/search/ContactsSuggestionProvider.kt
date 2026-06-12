package com.githu.stephenwanjala.composecontacts.contacts.contactslist.data.search

import android.content.SearchRecentSuggestionsProvider

/**
 * Backs the recent-search history shown in the contacts search bar.
 *
 * [SearchRecentSuggestionsProvider] persists submitted queries in its own
 * SQLite-backed content provider, automatically de-duplicating and ordering by
 * recency. We read them back through [RecentSearchesRepository] and write them
 * with [android.provider.SearchRecentSuggestions].
 *
 * Registered in the manifest with [AUTHORITY]; nothing else references it
 * directly, so the authority just has to stay in sync between the two.
 */
class ContactsSuggestionProvider : SearchRecentSuggestionsProvider() {
    init {
        setupSuggestions(AUTHORITY, MODE)
    }

    companion object {
        const val AUTHORITY = "com.githu.stephenwanjala.composecontacts.search.suggestions"
        const val MODE = DATABASE_MODE_QUERIES
    }
}
