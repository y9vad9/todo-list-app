# Desktop App

This is the Desktop entry point for the Todolist Kotlin Multiplatform application. It shares business logic and UI with other platforms (Android, iOS) using the common modules: `compose-ui`, `presentation`, `domain`, and `integration`.

## Running the App

You can run the desktop application in two ways: directly as a JVM application or by building a native package using the Compose Desktop Gradle plugin.

### Option 1: Run from Gradle

To run the app using the JVM (recommended for development):
```bash
./gradlew :platform:desktop:run
```

This launches the app using the Compose Desktop runtime on your current platform.

### Option 2: Build Native Executable

To generate a native, distributable desktop application (e.g., `.dmg` on macOS, `.exe` on Windows):
```bash
./gradlew :platform:desktop:packageDistributionForCurrentOS
```

This will produce a native installer/binary in:
```
platform/desktop/build/compose/binaries/
```

The output format depends on your operating system.

## Prebuilt Releases

If you're using **macOS**, you may find a prebuilt native version available on the [Releases page](../../releases). This can be downloaded and run without building from source.

## Shared Modules

## Shared Modules

This module integrates with the following shared logic layers:
- `domain`: business logic and domain model
- `presentation`: MVI-based UI logic
- `compose-ui`: cross-platform Compose UI components
- `integration`: data layer and repository implementations
- `dependencies`: Dependency Injection module to work more effectively across platforms, and on Android.