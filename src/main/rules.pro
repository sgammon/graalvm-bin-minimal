-dontshrink
-dontoptimize
-dontobfuscate
-allowaccessmodification
-dontnote com.oracle.**
-dontnote com.fasterxml.**
-dontnote com.typesafe.**
-dontnote org.yaml.**
-dontnote picocli.**
-dontnote org.graalvm.**
-dontnote kotlin.**
-dontnote io.micronaut.**

-libraryjars  <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)

-keep class com.sun.jna.** { *; }
-keep class * implements com.sun.jna.** { *; }
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeVisibleTypeAnnotations,AnnotationDefault

-dontwarn io.micronaut.**
-dontwarn io.micrometer.**
-dontwarn jakarta.**
-dontwarn reactor.**
-dontwarn kotlin.reflect.**
-dontwarn org.codehaus.**
-dontwarn org.graalvm.**
-dontwarn com.oracle.truffle.**
-dontwarn com.fasterxml.**
-dontwarn com.typesafe.**
-dontwarn org.yaml.**
-dontwarn ch.qos.logback.**
-dontwarn com.oracle.svm.core.annotate.Delete

-keep class module-info
-keepattributes Module*
-keep class kotlin.Metadata

-keep class io.micronaut.** { *; }
-keep enum io.micronaut.** { *; }
-keep interface io.micronaut.** { *; }
-keep @interface io.micronaut.** { *; }

-keep class picocli.CommandLine { *; }
-keep class picocli.CommandLine$* { *; }

-keepclassmembers class * extends java.util.concurrent.Callable {
    public java.lang.Integer call();
}

-keepclassmembers class * {
    @javax.inject.Inject <init>(...);
    @picocli.CommandLine$Option *;
}

-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep public class * {
  public * ;
}

-keep public interface * {
  public * ;
}

-keep class java.time.** {
   public java.time.** parse(java.lang.CharSequence);
   public java.time.** of(java.lang.String);
}

-keep class com.example.DemoCommand {
  public static void main(java.lang.String[]);
}
