# Shared Platform Module

The `:platform:shared` module contains the shared code that works across all platforms (Android, iOS, Desktop). It includes common business logic, UI components, and platform bindings.

## Purpose

This module includes:
- **Kotlin/Native bindings for Compose** in the `iosMain` source set to make Compose work on iOS.
- Shared **business logic** and **domain models** used across all platforms, defined in the `:domain` module.
- Shared **UI components** and **view models** that can be used on Android, iOS, and Desktop.

### Why This Module Exists
The `:platform:shared` module allows the same code to be used across different platforms while keeping platform-specific details separate. This reduces duplication and helps maintain consistency in the app’s logic and UI.