# PIA Android Native Terminal

Native Android trading terminal application for the **PIA Engine** ecosystem.

## Architecture

- **UI Layer**: Jetpack Compose (Modern declarative UI, Material 3 Dark theme).
- **Chart Engine**: TradingView Lightweight Charts Android (`com.tradingview:lightweightcharts:5.2.0`).
- **High-Performance Core (NDK)**: `core-native/` Rust crate compiled to `libpia_mobile_core.so` via JNI:
  - Micro-second lock-free tick ring buffer.
  - 16-50ms tick conflator / OHLC coalescing engine.
  - Zero-allocation rolling EMA & RSI indicators.
- **Networking**: OkHttp WebSocket connecting directly to `wss://api-engine.wign.dev/api/v1/ws`.

## Project Structure

```text
pia-android/
├── core-native/               # Rust NDK high-performance tick core
│   ├── src/
│   │   ├── conflator.rs       # OHLC tick coalescing
│   │   ├── indicators.rs      # High-performance EMA/RSI
│   │   ├── ringbuffer.rs      # Thread-safe lock-free buffer
│   │   └── lib.rs             # JNI export bindings
│   └── Cargo.toml
├── app/                       # Android application module
│   ├── src/main/kotlin/dev/wign/pia/
│   │   ├── data/              # WebSocket & JNI Bridge
│   │   ├── ui/                # Compose screens, Chart & Navigation
│   │   └── MainActivity.kt
│   └── build.gradle.kts
└── .github/workflows/ci.yml   # Continuous Integration & NDK build
```

## Prerequisites & Development

1. **Android Studio**: Ladybug / Meerkat or later (JDK 17+).
2. **Rust & Android NDK**:
   ```bash
   rustup target add aarch64-linux-android x86_64-linux-android
   cargo install cargo-ndk
   ```
3. **Build Native Libraries**:
   ```bash
   cd core-native
   cargo ndk -t arm64-v8a -t x86_64 -o ../app/src/main/jniLibs build --release
   ```
