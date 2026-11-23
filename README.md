# Caller

A privacy-first Android caller protection app built with Kotlin and Jetpack Compose.

> **Current milestone: Android foundation + local call blocklist.** This is an early development build, not yet a complete Truecaller replacement.

## Working in this milestone
- Native Compose app shell with Home, Blocklist, and Settings.
- Add and remove numbers from a private, on-device blocklist.
- Android Call Screening service and user-controlled role request.
- Incoming calls matching an explicitly blocked number can be rejected.
- Formatting is normalized before exact local matching.
- No contact upload, call-log upload, SMS access, analytics SDK, or remote lookup is included.

## Requirements
- Android Studio and JDK 17.
- Android SDK Platform 37 and Build Tools 36.0.0.
- Gradle 9.6.0 (bootstrap scripts download it on first run).
- Minimum Android version: 8.0 / API 26.
- Automatic call screening requires Android 10 / API 29+ and the user's approval.

## Build
Linux / macOS: `./gradlew :app:assembleDebug`

Windows: `gradlew.bat :app:assembleDebug`

Debug APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Enable protection
1. Install and open Caller on Android 10+.
2. Tap **Enable call protection** on Home.
3. Approve the Android system Call Screening role prompt.
4. Add numbers in Blocklist.
5. Incoming calls matching the local list can now be rejected.

Unknown numbers not on the blocklist are allowed. No unverified number is labelled as spam.

## Architecture
- `MainActivity.kt`: activity and Android role request.
- `CallerApp.kt`: Compose UI and navigation.
- `data/PhoneNumberNormalizer.kt`: conservative normalization.
- `data/BlockedNumberStore.kt`: local private storage.
- `telephony/CallerScreeningService.kt`: Android call screening.
- `ui/theme/Theme.kt`: Material theme.

## Roadmap
1. **Foundation (current):** app shell, local blocklist, Android screening role.
2. **Contacts and recents:** user-consented contacts and appropriately scoped call-history support.
3. **Caller identification:** lookup using a verified, lawfully sourced dataset.
4. **Spam intelligence:** explainable reports, confidence levels, user-controlled submissions.
5. **Smart dialer:** optional default-dialer experience following Android role requirements.
6. **Release hardening:** accessibility, localization, tests, privacy review, and Play policy checks.

## Privacy principles
- Keep user-created blocklist entries on-device in this milestone.
- Request Android roles only after the user taps the relevant control.
- Never identify a number as a scammer without reliable evidence.
- Do not scrape private personal data or expose private phone numbers.
- Explain data sources and any future network lookup clearly.

## Limitations
Arbitrary unknown-number identification requires a reliable, lawful data source and may require network access. This starter does not include such a database and does not claim to identify every caller. Device-maker call-screening behavior also varies; test on real devices before release.
