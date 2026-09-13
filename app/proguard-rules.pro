# TradingView Lightweight Charts proguard
-keep class com.tradingview.lightweightcharts.** { *; }

# JNI Native Bridge
-keepclasseswithmembernames class dev.wign.pia.data.NativeBridge {
    native <methods>;
}

# Moshi rules
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.squareup.moshi.* <methods>;
    @com.squareup.moshi.* <fields>;
}
