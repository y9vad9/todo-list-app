-keep class androidx.compose.** { *; }
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }

-keep class kotlin.Metadata

-keep class kotlinx.coroutines.internal.MainDispatcherFactory { *; }
-keep class kotlinx.coroutines.CoroutineExceptionHandler { *; }
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory { *; }
-keep class kotlinx.coroutines.android.AndroidExceptionPreHandler { *; }

-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Preserve SafeContinuation's volatile fields
-keepclassmembernames class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

-keep class **.sqldelight.** { *; }
-keep interface **.sqldelight.** { *; }

-keep class kotlinx.serialization.** { *; }
-keep class kotlinx.serialization.internal.** { *; }

-keep class org.koin.** { *; }

-keep class com.y9vad9.todolist.composeui.navigation.** { *; }