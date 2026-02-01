# UPI Safety Scanner - India

A production-ready Android application built with Kotlin and Jetpack Compose for scanning and analyzing UPI QR codes for potential security risks.

## Features

### Free Tier
- 10 QR code scans per day
- View last 20 scan history items
- Risk assessment for scanned UPI QR codes

### Pro Tier (₹199/year)
- Unlimited QR code scans
- Full scan history access
- CSV export functionality
- Ad-free experience

## Technical Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material 3
- **Architecture:** MVVM (Model-View-ViewModel)
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Database:** Room
- **Camera:** CameraX
- **Barcode Scanning:** ML Kit
- **Billing:** Google Play Billing Library v6+

## Key Components

### Risk Assessment Engine
Offline risk analysis based on:
- Suspicious VPA patterns
- Name mismatches
- Blacklisted keywords
- Suspicious URL detection

### Screens
1. **Splash Screen** - Entry point with entitlement verification
2. **Home Screen** - Main hub with scan button and usage counter
3. **Scanner Screen** - Real-time QR code scanning with CameraX
4. **Result Sheet** - Detailed UPI information with risk badges
5. **History Screen** - Past scans with search functionality
6. **Paywall Screen** - Subscription purchase and restoration
7. **Settings Screen** - Language toggle (English/Hindi) and preferences

### Architecture
```
app/
├── data/
│   ├── local/ (Room Database entities and DAOs)
│   └── repository/ (Billing and Scan repositories)
├── domain/
│   └── model/ (Business models)
├── ui/
│   ├── screens/ (Composable screens and ViewModels)
│   └── theme/ (Material 3 theme configuration)
└── utils/ (UPI parser and risk analyzer)
```

## Building the Project

1. Clone the repository
2. Open in Android Studio Giraffe or later
3. Sync Gradle
4. Run on an emulator or physical device

```bash
./gradlew build
```

## Billing Configuration

The app uses Google Play Billing v6+ with the following subscription:
- **Product ID:** `upi_pro_yearly`
- **Type:** Auto-renewable yearly subscription
- **Price:** ₹199/year

To test billing, you'll need to:
1. Configure the product in Google Play Console
2. Add test accounts
3. Use a signed release build

## Permissions

- **CAMERA** - Required for QR code scanning
- **INTERNET** - Required for Google Play Billing
- **BILLING** - Required for in-app purchases

## Multi-language Support

The app supports:
- English (default)
- Hindi (हिंदी)

## License

Copyright © 2024 UPI Safety Scanner

All rights reserved.