# Support Library
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Dagger
-dontwarn dagger.internal.codegen.**

-keep class **$$InjectAdapter** { *; }
-keep class **$$ModuleAdapter** { *; }