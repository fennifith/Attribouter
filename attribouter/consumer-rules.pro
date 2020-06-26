##---------------Begin: Attribouter-specific configuration --------

# For xml-specified drawable resources
-keep class me.jfenn.attribouter.R$*
-keepclassmembers class me.jfenn.attribouter.R$* {
    public static <fields>;
}

# For wedge construction (from xml parser)
-keep class * extends me.jfenn.attribouter.wedges.Wedge

##---------------End: Attribouter-specific configuration ----------

##---------------Begin: proguard configuration for Ktor -------

-keep class io.ktor.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn io.netty.**
-dontwarn com.typesafe.**
-dontwarn org.slf4j.**

# Coroutines: https://github.com/ktorio/ktor/issues/1354
-keepclassmembers class kotlinx.** {
    volatile <fields>;
}
-keepclassmembers class io.ktor.** {
    volatile <fields>;
}

##---------------End: proguard configuration for Ktor ---------

##---------------Begin: proguard configuration for gitrest -------

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.yourcompany.yourpackage.**$$serializer { *; } # <-- change package name to your app's
-keepclassmembers class me.jfenn.gitrest.** { # <-- change package name to your app's
    *** Companion;
}
-keepclasseswithmembers class me.jfenn.gitrest.** { # <-- change package name to your app's
    kotlinx.serialization.KSerializer serializer(...);
}

##---------------End: proguard configuration for gitrest ---------
