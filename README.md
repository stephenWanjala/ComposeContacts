# ComposeContacts

A contacts app built entirely with Jetpack Compose and Material 3.
It reads the device's contacts, alphabetically grouped
list, and supports searching (with recent-search history), viewing contact
details, generating a shareable QR code for a contact, and adding new contacts.

## Features

- **Alphabetically grouped list** with sticky section headers and avatars
  (contact photo via [Coil], or a tinted initial fallback).
- **Material 3 search** using `SearchBar`:
  - Live filtering by name, phone number, or email.
  - **Recent searches** backed by Android's `SearchRecentSuggestionsProvider`,
    so previously submitted queries are suggested again and can be cleared.
- **Contact details** screen with shared-element transitions from the list.
- **QR code** generation for sharing a contact.
- **Add new contact** flow.
- **Pull-to-refresh** and graceful empty / loading states.
- **Runtime permission handling** for reading and writing contacts.

## Tech stack

| Area | Choice |
| --- | --- |
| Language | Kotlin 2.4.0 |
| UI | Jetpack Compose (BOM 2026.05.01), Material 3 (`1.5.0-alpha21`) |
| Architecture | MVVM with `ViewModel` + Kotlin `Flow`/`StateFlow` |
| Dependency injection | [Koin] 4.2.1 |
| Navigation | Navigation Compose with type-safe routes (`@Serializable`) |
| Image loading | [Coil] 3.5 |
| Async | Kotlin Coroutines |



### How contacts are loaded

`ContactsDataSource` queries the `ContactsContract` provider on a background
dispatcher: one pass over the phone table to build each contact (id, name,
photo, de-duplicated numbers), then a single flat scan of the email table that
is joined in memory. Results are exposed through `ContactsListViewModel` as a
`StateFlow` and grouped by their leading initial for the list UI.

### How recent searches work

`ContactsSuggestionProvider` (a `SearchRecentSuggestionsProvider` subclass,
registered in the manifest) persists submitted queries. `RecentSearchesRepository`
saves queries via `SearchRecentSuggestions` and reads them back by querying the
provider's `search_suggest_query` URI. The ViewModel surfaces them as a debounced
`recentSuggestions` flow that the `SearchBar` shows when the query is blank.

## Requirements

- Android Studio (latest stable recommended)
- Android Gradle Plugin 9.2.1 / Kotlin 2.4.0
- JDK 17+
- **minSdk 24**, **compile/target SDK 37**

## Building & running

```bash
# Build a debug APK
./gradlew assembleDebug

# Install on a connected device / emulator
./gradlew installDebug
```

Or open the project in Android Studio and run the `app` configuration.

On first launch the app requests the **READ_CONTACTS** permission (and
**WRITE_CONTACTS** for adding contacts); grant it to see your contacts.

## Permissions

| Permission | Why |
| --- | --- |
| `READ_CONTACTS` | Load and display contacts |
| `WRITE_CONTACTS` | Add new contacts |
| `READ_MEDIA_IMAGES` / `READ_EXTERNAL_STORAGE` | Load contact photos |
| `INTERNET` | Coil image pipeline |

## License

```
Copyright 2026 Wanjala Stephen

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

See the [LICENSE](LICENSE) file for the full text.

[Coil]: https://coil-kt.github.io/coil/
[Koin]: https://insert-koin.io/
