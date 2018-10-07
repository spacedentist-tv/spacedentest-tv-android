# Support Library
-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Dagger
-dontwarn dagger.internal.codegen.**
-dontobfuscate

-keep class **$$InjectAdapter** { *; }
-keep class **$$ModuleAdapter** { *; }