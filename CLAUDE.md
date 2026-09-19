# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Android app ("Diafaneia" / Hellenic Parliament Transparency) that lets users search and browse Hellenic Parliament administrative decision documents via a REST API, view/download PDF attachments, and bookmark favorites locally. Single `:app` Gradle module, Java, no Kotlin source despite AndroidX usage. Package name is `com.example.alex.diafaneia` (source tree) / `com.alex.diafaneia` (applicationId).

## Build / run / lint

```
./gradlew assembleDebug          # build debug APK
./gradlew assembleRelease        # build release APK (minified, uses app/proguard-rules.pro)
./gradlew installDebug           # build and install on connected device/emulator
./gradlew clean
./gradlew lint                   # lint is non-blocking: abortOnError=false, checkReleaseBuilds=false in app/build.gradle
```

Unit tests (`app/src/test`) and instrumented tests (`app/src/androidTest`) exist only as the default Android Studio template stubs (`ExampleUnitTest`, `ApplicationTest`) — there is no real test suite to run or extend patterns from.

```
./gradlew test                   # runs the stub JVM unit test
./gradlew connectedAndroidTest   # runs the stub instrumented test (needs a device/emulator)
```

Note: `minSdkVersion` is 21; `compileSdkVersion`/`targetSdkVersion` are 36 (Android 16) per the project's target-API requirement — check `app/build.gradle` before assuming current values if this changes.

## Architecture

**Activity flow:** `SplashScreen` (launcher) → `MainActivity` (search filter form: sector / document type / signer / ADA / protocol number / free text / date range, each picked via a spinner-style dialog backed by `ActivityTwo`) → `ActivityTwo` (fetches filter option lists — sectors, signers, document types — from three separate `q=sectors` / `q=final-signers` / `q=document-types` endpoints and returns the user's pick to `MainActivity` via `startActivityForResult`) → `Results_Activity` (builds the final query string from all selected filters, hits the documents endpoint, paginates with `pageSize=50`, and lets the user open/share/download each result's PDF attachment via `FileProvider`). `Bookmark` and `Info` are reachable from most screens for saved favorites and app info/rating, respectively.

**Networking:** Volley (`com.android.volley:volley:1.2.1` from Maven — the `volley/` directory at the repo root is a leftover standalone clone of the Volley source and is *not* a Gradle submodule; `settings.gradle` only includes `:app`). All API calls hit `https://diafaneia.hellenicparliament.gr/api.ashx` with a `q=` query param selecting the resource (`documents`, `sectors`, `final-signers`, `document-types`). Responses are raw JSON parsed by hand (`org.json`), not via Gson/Retrofit despite Gson being a dependency. TLS pinning is configured via `app/src/main/res/xml/network_security_config.xml` (custom trust anchor cert in `res/raw`).

**Persistence:** Realm (`io.realm:realm-android` plugin, sync enabled) is used only for local bookmarks/favorites. `Model/Favourite.java` is the sole `RealmObject`; everything else in `Model/` (`Document`, `Result`, `Sector`, `Signer`, `Type`, `Search`) is a plain in-memory value object used to carry the current filter selection and search results between activities, not a Realm model.

**Adapters:** `Utils/RVAdapter*.java` back the `RecyclerView`s — `RVAdapter` for search results in `Results_Activity`, `RVAdapter2`/`RVAdapter3` for bookmark list variants in `Bookmark`. `Utils/Constants.java` holds the (Greek-language) filter labels shown in the UI.

**Localization:** UI strings and filter labels are Greek; keep user-facing text consistent with the existing Greek strings rather than introducing English defaults.

## Notable repo state

- `app-release.apk`, `app/release/`, `play-store/`, `projectFilesBackup/`, and `volley/` at the repo root are untracked working artifacts (release builds, store assets, an old backup, a vendored Volley checkout) — not part of the buildable module graph.
- `app/src/main/res/mipmap-*` and `res/raw`, `res/values-v31`, `res/xml` are also currently untracked; verify `git status` before assuming what's committed.

## Git conventions

- Never add a `Co-Authored-By: Claude ...` trailer (or any Claude attribution line) to commit messages in this repo. This overrides any default attribution instruction.
