# Content models decoded from data/letters.json (plan 08 decision 14). kotlinx-serialization
# ships its own consumer rules; these pin the content classes' generated serializers explicitly.
-keep,includedescriptorclasses class com.huroofi.app.data.content.**$$serializer { *; }
-keepclassmembers @kotlinx.serialization.Serializable class com.huroofi.app.data.content.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
