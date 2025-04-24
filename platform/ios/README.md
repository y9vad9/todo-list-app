# iOS App

This module represents the iOS version of the Todolist application, built using Kotlin Multiplatform and Swift. It is implemented as a native Xcode project that integrates with the shared KMP code via a framework built from the `:platform:shared` Gradle module.

## Prerequisites

Before you begin:

- Install the latest version of **Xcode**
- Make sure **iOS SDKs** are installed
- Install **Kotlin Multiplatform Mobile plugin** for Gradle if you're working from Android Studio or IntelliJ IDEA

## Project Structure

The iOS app is located under the `platform/ios` directory. It is a standard Xcode project that links to the shared Kotlin logic using a framework compiled from the shared module.

## Building the iOS App

The integration process between the shared KMP module and the Xcode project is not fully automated and requires the following steps:

### 1. Generate and Copy the KMP Framework

Run the appropriate Gradle task from the root project directory:

For a **debug** build:
```bash
./gradlew :platform:shared:copyDebugFrameworkToXcode
```

For a **release** build:
```bash
./gradlew :platform:shared:copyReleaseFrameworkToXcode
```

This copies the compiled Kotlin Multiplatform framework to a location where Xcode can access it (usually under `platform/ios/Frameworks`).

### 2. Open the Xcode Project

Open the iOS app in Xcode:

```bash
open platform/ios/Todolist.xcodeproj
```

Make sure:
- The framework has been correctly linked in the project’s **Linked Frameworks and Libraries**
- The proper **build target** (e.g., simulator or physical device) is selected

### 3. Build and Run

Once the framework is copied and the project is open:

- Select the correct device or simulator
- Press **Cmd+R** to build and run the app from Xcode

## Additional Notes

- The app reuses the same business logic as other platform targets (Android, Desktop, CLI) via Kotlin Multiplatform
- UI and navigation are written in Swift/SwiftUI and integrate with Compose via the shared `ComposeUIViewController`
- If you're only interested in running the app, check the [Releases page](https://github.com/y9vad9/todo-list-app/releases) for a prebuilt `.ipa` (not available for now)