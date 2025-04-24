# Android App

This is the Android entry point for the **Todolist** Kotlin Multiplatform application. It shares logic and UI with other platforms (iOS, Desktop) via the shared `compose-ui`, `presentation`, `domain`, and `integration` modules.

## Getting Started

### Option 1: Install from GitHub Releases
If you're only interested in using the app and not contributing to development, the easiest way is to:

1. Visit the [Releases page](https://github.com/y9vad9/todo-list-app/releases).
2. Download the latest `.apk` file.
3. Install it on your Android device.

### Option 2: Build Locally

#### Prerequisites
- **Android Studio (latest stable release)**  
  Recommended for easiest setup with automatic configuration and IDE support.

Or, if you're using another JetBrains IDE (e.g., IntelliJ IDEA):

- Make sure **Android SDK** and **Android Plugin** are properly configured.
- A **physical device** or emulator should be connected and running.

#### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/YOUR_USERNAME/todolist.git
   cd todolist
   ```

2. Open the project in Android Studio.

3. Click **Run ▶️** to deploy the app on a connected Android device or emulator.

   Alternatively, from the root of the project, you can use Gradle:
   ```bash
   ./gradlew :platform:android:installDebug
   ```

   > You must have an Android device or emulator running for this to succeed.

## Shared Code

This module reuses logic and UI from:
- `domain`: business logic and domain models (DDD style)
- `presentation`: MVI logic for UI
- `compose-ui`: cross-platform Jetpack Compose UI components
- `integration`: platform-specific data access

## Notes

- This module uses **Jetpack Compose** (through Jetbrains Compose Multiplatform) for UI.
- Compatible with Android API 26+ (Android 8.0 and higher). 

## Feedback & Contributions

Feel free to [open an issue](https://github.com/y9vad9/todo-list-app/issues) or submit a PR if you encounter a bug or want to contribute!