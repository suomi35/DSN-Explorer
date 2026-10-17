# Keep line numbers for readable crash stack traces.
-keepattributes SourceFile,LineNumberTable

# kotlinx.serialization: keep serializers for the JSON models used against the
# JPL Horizons API so R8 cannot strip reflective serializer lookup.
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.holdenisacat.dsnexplorer.repository.**$$serializer { *; }
-keepclassmembers class com.holdenisacat.dsnexplorer.repository.** {
    *** Companion;
}
-keepclasseswithmembers class com.holdenisacat.dsnexplorer.repository.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor on OkHttp pulls optional dependencies that are not shipped.
-dontwarn org.slf4j.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
