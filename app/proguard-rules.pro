# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html



# keep the class and specified members from being removed or renamed
-keep class com.libonline.MainActivity { void onCreate(android.os.Bundle); }

# keep the specified class members from being removed or renamed
# only if the class is preserved
-keepclassmembers class com.libonline.MainActivity { void onCreate(android.os.Bundle); }

# keep the class and specified members from being renamed only
-keepnames class com.libonline.MainActivity { void onCreate(android.os.Bundle); }

# keep the specified class members from being renamed only
-keepclassmembernames class com.libonline.MainActivity { void onCreate(android.os.Bundle); }



# keep the class and specified members from being removed or renamed
-keep class com.libonline.module.Database { *; }

# keep the specified class members from being removed or renamed
# only if the class is preserved
-keepclassmembers class com.libonline.module.Database { *; }

# keep the class and specified members from being renamed only
-keepnames class com.libonline.module.Database { *; }

# keep the specified class members from being renamed only
-keepclassmembernames class com.libonline.module.Database { *; }