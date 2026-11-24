# Caller

A privacy-first Android caller protection app built with Kotlin and Jetpack Compose.

> **Current milestone: v0.3.0 — on-device contact caller matching.** Caller is under active development and is not yet a complete Truecaller replacement.

## Implemented
- Native Compose app with Home, Contacts, Blocklist, and Settings tabs.
- Local phone-contact directory with name and phone-number search.
- Incoming-number matching through Android's local Contacts Provider on Android 11+ when Contacts permission is granted and Caller is the selected call-screening app.
- One private latest-match record that the user can clear.
- Optional notification when an incoming number matches a saved contact (Android 13+ notification permission required).
- Contact access is requested only when the user taps **Allow contact access**.
- Contact rows are read from Android's Contacts Provider and held in memory for the active screen.
- No contact upload, call-log upload, SMS access, analytics SDK, or remote phone-number lookup.
- Add and remove numbers from a private, on-device blocklist.
- Android Call Screening service and user-controlled role request.
- Incoming calls matching an explicitly blocked number can be rejected when Caller holds Android's screening role.

## Requirements
- Android Studio and JDK 17.
- Android SDK Platform 37.0 and Build Tools 37.0.0.
- Android Gradle Plugin 9.5.0 and Gradle 9.6.0.
- Minimum Android version: 8.0 / API 26.
- Automatic call screening requires Android 10 / API 29+ and the user's approval.

## Build
Linux / macOS: `./gradlew :app:testDebugUnitTest :app:assembleDebug`

Windows: `gradlew.bat :app:testDebugUnitTest :app:assembleDebug`

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.

A GitHub Actions workflow also runs JVM unit tests and assembles the debug APK on pushes and pull requests.

## Contacts privacy
1. Open the Contacts tab.
2. Read the explanation and tap **Allow contact access**.
3. Approve or deny the Android permission prompt.
4. Search saved contact names or phone-number digits.

The app does not read the device contacts before permission is granted. Contact results are not sent to a server. Revoking the permission in Android settings prevents further reads. During incoming call screening, Caller checks the incoming number against the local Contacts Provider. A match is stored as the single latest result and shown in the Caller ID tab. On Android 13+, the user can separately grant notification permission for optional match alerts.

## Incoming caller matching

1. Grant Contacts permission from the Contacts tab.
2. On Android 10+, select Caller as the call-screening app.
3. On Android 11+, when Caller holds the call-screening role and Contacts permission, Android can deliver contact calls to the service. When an incoming number matches a saved contact, Caller stores the name and number as the latest local match. Android 10's call-screening callback is limited to numbers that are not already in the user's contacts.
4. Open the Caller ID tab to see the result or clear it.
5. On Android 13+, optionally enable notifications in Settings.

Contact matches are informational only; they never cause a call to be rejected. This milestone does not draw over Android's system incoming-call interface.

## Enable call protection
1. Install and open Caller on Android 10+.
2. Tap **Enable call protection** on Home.
3. Approve the Android system Call Screening role prompt.
4. Add numbers in Blocklist.
5. Incoming calls matching the local list can now be rejected.

Unknown numbers not on the blocklist are allowed. Contact search is currently an in-app directory; this milestone does not overlay contact names on the system incoming-call screen.

## Architecture
- `MainActivity.kt`: activity and Android role request.
- `CallerApp.kt`: Compose shell and tab navigation.
- `ContactsScreen.kt`: permission-gated contact search UI.
- `CallerIdScreen.kt`: latest matched caller detail and clear action.
- `data/ContactCallerLookup.kt`: single-number local Contacts Provider lookup.
- `data/LatestCallerMatchStore.kt`: private latest-match storage.
- `telephony/CallerMatchNotifier.kt`: optional Android match notification.
- `data/ContactsRepository.kt`: local Contacts Provider reader.
- `data/PhoneNumberNormalizer.kt`: conservative number normalization.
- `data/BlockedNumberStore.kt`: local private blocklist storage.
- `telephony/CallerScreeningService.kt`: Android call screening.
- `ui/theme/Theme.kt`: Material theme.

## Roadmap
1. **Foundation:** app shell, local blocklist, Android screening role.
2. **Local contacts:** contact directory and search.
3. **Current:** incoming caller matching against on-device contacts, optional match notification, and clearable latest-match detail.
4. **Caller identity:** verified business/public-number lookup with clear source and confidence labels.
5. **Spam intelligence:** transparent spam reports and user-controlled community submissions.
6. **Smart dialer:** optional default-dialer experience following Android role requirements.
7. **Release hardening:** accessibility, localization, tests, privacy review, and Play policy checks.

## Privacy principles
- Keep user-created blocklist entries on-device.
- Request Android permissions and roles only after the user taps the relevant control.
- Never identify a number as a scammer without reliable evidence.
- Do not upload address books or scrape private personal data.
- Explain data sources and any future network lookup clearly.

## Limitations
Arbitrary unknown-number identification requires a reliable, lawful data source and may require network access. This milestone matches incoming numbers to the user's own saved contacts (system contact-call delivery requires Android 11+); it does not contain an external reverse-lookup database. Android and device-maker call-screening behavior also varies, so test on real devices before release.
