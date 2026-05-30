# RootDetector

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE.txt)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84.svg)](https://developer.android.com)
[![API](https://img.shields.io/badge/API-23%2B-brightgreen.svg)](app/build.gradle.kts)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4.svg)](https://developer.android.com/jetpack/compose)

Native Android root detector with multi-layer detection for MagiskSU, KernelSU, APatch, SuperSU, and more.

> Disclaimer: This app is provided for educational and security research purposes only. The source code is open for contributions and audit.
## The app is "raw" and may contain some errors. The app is ready for additions and modifications.

## Features
- Bootloader status - Locked/Unlocked
- Collapsible customization Accent color picker, gradient background, auto-scan, vibration
- Save log - Export detection results as `.txt` file
- Device info sheet - Hardware, build, kernel, bootloader details

## Building

### Prerequisites

- Android Studio Ladybug (2024.2.1) or newer
- JDK 17+
- Android SDK 36/34

### Steps

```bash
git clone https://github.com/nzshit/Root-Detector.git
cd Root-Detector

# Debug build
./gradlew assembleDebug

# Release build (with ProGuard)
./gradlew assembleRelease
```

The signed APK will be at `app/build/outputs/apk/release/app-release.apk`.

## License

```
Copyright 2026 nzshit

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Links

- [GitHub](https://github.com/nzshit/Root-Detector)
- [Telegram](https://t.me/+O5Zbw_y3tFE2YmYy)
