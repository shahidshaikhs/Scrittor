# Scrittor maintenance audit — 20 September 2026

## Assessment of the original code

**Overall: 4/10 for present-day maintainability and reliability.** This is a judgment of the checked-in code, not of the developer. It is a substantial personal app with a coherent interface, localization, offline storage, and useful features. The main problems are data integrity, lifecycle ownership, and the absence of meaningful automated tests; its age compounds those problems.

| Area | Original rating | Evidence |
| --- | --- | --- |
| Product/UI organization | 6/10 | Clear screens, themes, categories, search, export, and backup. Repeated styling and weak accessibility. |
| Architecture/maintainability | 4/10 | Helpful model/helper classes, but long activities combine UI, storage, authentication, and network coordination. |
| Data integrity | 3/10 | Destructive table reconstruction, copying open database files, restore overwriting newer data, stale category metadata. |
| Lifecycle/reliability | 3/10 | Closing the process-wide database when activities finish; editor finishes from `onPause`; missing null/error handling. |
| Authentication/privacy | 2/10 | Public intent extra bypasses the gate, shared editor bypasses it, plaintext PIN, deprecated fingerprint flow. |
| Tests/build reproducibility | 2/10 | Only template tests; obsolete Gradle/AGP, JCenter, duplicate Drive dependencies, suppressed release lint. |

## Repairs implemented

### Build and Android compatibility

- Gradle 9.3.1 and Android Gradle Plugin 9.1.1, tested with Android Studio's Java 25 runtime. Regenerated wrapper scripts/JAR and pinned the distribution SHA-256.
- Centralized Google/Maven Central repositories; removed JCenter and the unused Google Services plugin. The app does not use Firebase or generated Google Services resources.
- Added the required namespace, explicit exported entry points, and modern packaging configuration. Internal backup activity is no longer exported.
- Compile SDK 37, target SDK 36. Minimum SDK is now **23 / Android 6.0**, required by Billing 8.3.
- Updated AndroidX/UI, billing, Gson, Guava, Google sign-in, and Drive client dependencies. Removed desktop Jetty OAuth and duplicate Drive artifacts.
- Fixed an invalid resource ID, the `value-es-rUS` directory typo, missing default resources, AppCompat tint attributes, and nonconstant resource switches.
- Replaced legacy back overrides with AndroidX back dispatch and applied system-bar/keyboard insets for modern edge-to-edge layouts.
- Restored release lint checks; no lint baseline or blanket error suppression was introduced. The pre-existing MissingTranslation exemption remains.

### Notes, database, and categories

- In-place schema upgrades retain note/category row IDs, preserve content and stars, populate stable IDs, and translate old category references. Downgrades fail rather than destructively rebuilding user data.
- Database lifetime follows the process instead of the last activity. Singleton initialization and repository calls are synchronized; explicit test/maintenance close resets the singleton.
- Fixed cursor leaks on missing-note/category lookups, null category query arguments, mutable-projection assumptions, and ignored category-map keys.
- Updates return the actual note row ID. Batch writes use transactions and throw on insert failures.
- Numeric timestamp parsing replaces `Long.getLong` (which looks up a system property). Search tolerates null fields and uses locale-independent case folding.
- Adapter clicks ignore removed positions. Category names are compared consistently, UUID category IDs are not parsed as numbers, category edits propagate to notes, and deleting a category preserves its notes under the default category.
- Editor pause saves without finishing; repeated saves retain the same note ID. Explicit save failures keep the editor open. Merely reopening an unchanged note no longer advances its modification timestamp.
- Editor draft/category/star state survives activity recreation. Shared text remains plain text. Notification taps use the saved note ID, a stable channel, unique immutable pending intents, and notification permission checks.

### Backup and restore

- System document pickers replace obsolete external-storage permissions and hard-coded filesystem paths.
- New local/Drive backups use one versioned JSON snapshot containing notes and categories. Drive still reads both the old two-file format and legacy individual-file backups.
- New Drive files are created with their content in the same request. Lookup uses exact names, ignores trash, follows pagination, and selects the newest duplicate.
- Restore validates the full payload before writes, retains newer local edits, maps categories without changing existing identities, handles missing categories, and is repeatable without duplicating stable note IDs.
- Import also accepts legacy `FeedReader.db` selected through the file picker. It reads a temporary copy in read-only mode and merges notes; it never overwrites a live SQLite database. Category names/colors are recovered from notes. Category descriptions and empty categories from a separate `CategoriesHelper.db` are not imported by this path.
- Network failures and invalid/incomplete backups clear loading state and report failure. UI callbacks ignore destroyed activities; duplicate backup taps are blocked within the screen instance.
- Removed logging of note contents. Drive reads use explicit UTF-8 and close streams.

### Authentication and purchases

- Authentication is held in process memory, never trusted from intent extras. All app activities, including the exported share editor, pass through the gate and relock after backgrounding.
- AndroidX BiometricPrompt replaces the removed framework fingerprint API, with device-credential fallback. Protected windows use `FLAG_SECURE`.
- PIN verification uses a salted PBKDF2 verifier. A successful legacy PIN login migrates and removes the plaintext preference. Five incorrect attempts cause a 30-second delay. Canceling PIN setup preserves the existing protection.
- Billing 8 product queries handle failure/unavailable products; pending purchases are not consumed. Consumables are acknowledged through successful consumption, unfinished purchases are queried again, and success is shown only after Play confirms consumption.
- WebView accepts HTTPS URLs and disables file/content access and unnecessary JavaScript. Fixed the malformed Play Store fallback URL.

## Validation

Final verification succeeded on this machine with Java 25:

- `:app:assembleDebug` — passed; installable debug APK generated.
- `:app:assembleRelease` — passed, including release lint; unsigned APK generated.
- `:app:testDebugUnitTest` — **39 tests passed**, zero failures/errors/skips (38 regression cases plus the existing template test).
- `:app:lintDebug` — **zero errors, 314 warnings**. Remaining warnings are described below.
- `git diff --check` — passed.

Regression tests cover migrations, reopen/update identity, restore conflicts, malformed backup rejection, JSON and legacy SQLite imports, category maintenance, Drive client initialization, editor lifecycle, forged intents, share protection, PIN migration/throttling, and cancellation. Activity tests run under Robolectric at API 23, 28, and 36.

## Remaining limitations and follow-up work

- **No live Google account or Play purchase was exercised.** OAuth client/package/signing-certificate configuration, real Drive round trips, Play test purchases, and hardware biometric behavior require device/account testing. No emulator AVD was configured on this machine.
- Lint still reports legacy warnings (not errors), primarily missing accessibility labels, unused/duplicated resources, hard-coded text, layout cleanup, and newer dependency versions. A passing build does not certify a flawless UI.
- The database and exported backups are **not encrypted**. The app lock protects normal UI access; it is not a cryptographic vault. A four-digit PIN remains low entropy despite hashing and throttling.
- Notes/categories remain in separate SQLite databases. Note batches are atomic, but a storage failure midway through restore may leave newly added categories before notes commit. Existing notes are not deleted. A unified Room database would enable atomic cross-entity transactions.
- Restore resolves note conflicts by timestamp (local wins ties) and retains existing category metadata. Device clock skew can affect this rule. Backup/restore is not multi-device synchronization; deletion tombstones are not synchronized.
- Legacy local SQLite import cannot reconstruct independent category descriptions/empty categories from `CategoriesHelper.db`. Keep original backups if that metadata matters.
- Several list queries and editor saves still run on the UI thread. Large libraries need a repository/ViewModel refactor with background queries and paging.
- GoogleSignIn is still a legacy API even with an updated dependency. Move account authorization to Google's current authorization APIs in a separately tested integration change.
- Billing still has no backend purchase verification. This build supports consumable donations; do not repurpose the client-only flow for valuable entitlements without server verification.
- Some historical external/social links may no longer lead to maintained pages. Publishing also needs deliberate versioning, release signing, and store validation; neither publishing nor signing was performed here.

## Toolchain references

- [Gradle Java compatibility](https://docs.gradle.org/current/userguide/compatibility.html)
- [Android Gradle Plugin 9.1 compatibility](https://developer.android.com/build/releases/agp-9-1-0-release-notes)
- [Billing migration to version 8](https://developer.android.com/google/play/billing/migrate-gpblv8)
