-keep class com.arkivanov.decompose.extensions.compose.jetbrains.mainthread.SwingMainThreadChecker
-keep @kotlinx.serialization.Serializable class * {*;}
-dontwarn org.slf4j.Logger
-dontwarn org.slf4j.LoggerFactory
-keep class com.y9vad9.todolist.desktop.MainKt

-keep class kotlin.Metadata { *; }

-keepclassmembers class * {
    *** lambda*(...);
}

-keepclasseswithmembers class * {
    <init>(...);
}