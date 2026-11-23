# MyFirstApplication

## Project

Single-module Android e-commerce app (Java, `app/`), package `com.example.myfirstapplication`. minSdk 24, compileSdk/targetSdk 37, Java 11, AGP 9.5, Material Components 1.10 (Material 3 theme). UI strings are Vietnamese by default with an English override in `values-en/`.

## Commands

Run from the repo root (`gradlew` is not executable here, so use `sh gradlew`):

```bash
sh gradlew :app:assembleDebug          # build the debug APK
sh gradlew :app:processDebugResources  # fast check after editing res/ XML only
sh gradlew :app:testDebugUnitTest      # JVM unit tests
sh gradlew :app:testDebugUnitTest --tests "com.example.myfirstapplication.ExampleUnitTest"  # single test
sh gradlew :app:connectedDebugAndroidTest   # instrumented tests (needs device/emulator)
```

## Firebase setup (required to run)

`app/google-services.json` must be present locally or the build fails (`com.google.gms.google-services` plugin). The Firebase project must have **Authentication -> Email/Password enabled** and a **Firestore database created**.

## Architecture

Plain Activity-per-screen app:

- **Firebase Auth** — accounts (`RegisterActivity`, `LoginActivity`, `ForgotPasswordActivity`). Profile docs go to Firestore `users/{uid}`.
- **Firestore** — the shared catalog and orders. `MainActivity` reads collection `products` and seeds a sample list when it is empty (`seedSampleProductsToFirestore`); `CartActivity.placeOrder` writes to `orders`; `OrderHistoryActivity` reads `orders` back.
- **Local SQLite** (`CartDatabaseHelper`, `cart.db`) — the shopping cart only. It is cleared after a successful order is written to Firestore.
- **SharedPreferences `UserPrefs`** — the login session (`IS_LOGGED_IN`, `LOGGED_USER`, `DISPLAY_NAME`), written by `AuthHelper.saveSession`. `LOGGED_USER` is the user's email and is the key used to attribute and filter orders (`Order.username`).

Cross-cutting pieces:

- `NavigationHelper.setupBottomNav(activity)` wires the bottom navigation bar.
- `AuthHelper` holds shared validation and `openMain`.
- `App` applies saved dark/light preference at startup; `SettingsActivity` owns that preference and switches language via `AppCompatDelegate.setApplicationLocales`.
- `Product`, `Order` and `CartItem` are `Serializable` POJOs.

## Theming

Colors live in `values/colors.xml` with dark overrides in `values-night/colors.xml`. The app uses Material 3 theme and components.
