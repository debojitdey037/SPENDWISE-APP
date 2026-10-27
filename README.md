# Spendwise 💳✨

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.23-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Firestore%20%7C%20Auth-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com)
[![ML Kit](https://img.shields.io/badge/Google%20ML%20Kit-OCR%20Text%20Recognition-EA4335?style=for-the-badge&logo=google&logoColor=white)](https://developers.google.com/ml-kit)

> **Spendwise** is a modern, intuitive, and full-featured Android expense tracking and personal finance management app built with **Jetpack Compose** and **Material 3**. Effortlessly monitor daily expenses, scan receipts using on-device ML OCR, split bills with friends, manage categorical budgets, and gain insights through visual analytics.

---

## 🌟 Key Features

### 📊 Real-Time Dashboard
- **Financial Overview**: Instant snapshot of total balance, monthly expenditures, and savings.
- **Recent Activity**: Quick access to recent transactions with category icons and color-coded status.
- **Quick Actions**: Add expenses, scan receipts, or split bills in a single tap.

### 📷 Smart Receipt Scanning (OCR)
- **On-Device Machine Learning**: Powered by **Google ML Kit Text Recognition** for high-speed, offline-capable scanning.
- **Multi-Source Capture**: Capture receipts in real time using the device camera or select images from the gallery.
- **Automated Extraction**: Parses total amounts, dates, and merchant information automatically with manual override options.

### 👥 Bill Splitting & Group Expenses
- **Friend Management**: Keep track of shared meals, group trips, and recurring group expenses.
- **Who Owes Whom**: Clear balance tracking between you and your friends with 1-click settlement.

### 🎯 Budget Planning & Alerts
- **Category Limits**: Set custom budgets for Groceries, Dining, Entertainment, Travel, Bills, and more.
- **Visual Progress Indicators**: High-contrast progress bars to alert you when nearing or exceeding monthly spending thresholds.

### 📈 Visual Analytics & Insights
- **Spending Breakdowns**: Category-based distribution and expenditure trends over time.
- **Actionable Insights**: Understand your money habits and optimize your monthly savings.

### 🎁 Curated Offers & Rewards
- **Exclusive Deals**: Integrated discounts and cashback offers curated to maximize savings on everyday purchases.

### ☁️ Cloud Sync & Offline Reliability
- **Firebase Firestore**: Seamless, real-time synchronization across devices.
- **Offline Persistence**: Local caching powered by **Room Database** and Kotlin Coroutines/Flows ensures the app is always fast and responsive, even without an internet connection.
- **Firebase Authentication**: Secure user profiles with cloud data backup.

---

## 🛠️ Tech Stack & Architecture

| Layer | Technology |
|---|---|
| **Language** | [Kotlin](https://kotlinlang.org/) (100%) |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/) |
| **Architecture** | MVVM (Model-View-ViewModel) + Clean Architecture principles |
| **Asynchronous** | Kotlin Coroutines & `StateFlow` / `Flow` |
| **Backend & Cloud** | [Firebase Authentication](https://firebase.google.com/docs/auth), [Cloud Firestore](https://firebase.google.com/docs/firestore), [Cloud Messaging (FCM)](https://firebase.google.com/docs/cloud-messaging) |
| **Local Storage** | [Room Database](https://developer.android.com/training/data-storage/room) via KSP |
| **Computer Vision / ML** | [Google ML Kit Text Recognition](https://developers.google.com/ml-kit/vision/text-recognition) |
| **Dependency Management** | Gradle Version Catalogs (`gradle/libs.versions.toml`) |
| **Min / Target SDK** | Min SDK 24 (Android 7.0) • Target SDK 34 (Android 14) |

---

## 📂 Project Structure

```text
Spendwise/
├── app/
│   ├── google-services.json          # Firebase configuration file
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/com/example/spendwise/
│   │   │   │   ├── MainActivity.kt               # Main entry point & bottom navigation
│   │   │   │   ├── data/
│   │   │   │   │   └── FirestoreManager.kt       # Firebase Firestore cloud data layer
│   │   │   │   ├── model/
│   │   │   │   │   ├── Expense.kt                # Expense data model
│   │   │   │   │   └── Friend.kt                 # Friend / bill splitting model
│   │   │   │   ├── ui/
│   │   │   │   │   ├── components/               # Reusable UI widgets (StatCard, ExpenseItem, OfferCard)
│   │   │   │   │   ├── screens/                  # Compose Screens (Dashboard, Scan, Split, Budget, Analytics, Offers, Login, Welcome)
│   │   │   │   │   └── theme/                    # Color palette, Typography, Shape & Theme definition
│   │   │   └── res/                              # Drawables, mipmaps, strings, and theme XMLs
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml                        # Centralized dependency catalog
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio**: Iguana (2023.2.1) / Jellyfish / Koala or newer.
- **JDK**: Java 17 or higher.
- **Android Device / Emulator**: Running Android API 24 (Nougat) or later with Google Play Services enabled.

### Installation & Setup

1. **Clone the Repository**
   ```bash
   git clone https://github.com/debojitdey037/SPENDWISE-APP.git
   cd SPENDWISE-APP
   ```

2. **Open in Android Studio**
   - Launch Android Studio.
   - Select **Open** and choose the `SPENDWISE-APP` directory.
   - Allow Gradle to sync dependencies.

3. **Firebase Setup**
   - The repository includes Firebase integration. If configuring your own Firebase project:
     1. Create a project in the [Firebase Console](https://console.firebase.google.com/).
     2. Add an Android app with package name `com.example.spendwise`.
     3. Download your `google-services.json` and place it in the `app/` directory.
     4. Enable **Authentication** (Email/Password or Anonymous) and **Cloud Firestore**.

4. **Build and Run**
   - Select your target device or emulator.
   - Click **Run** (`Shift + F10`) or execute via Gradle:
     ```bash
     ./gradlew assembleDebug
     ```

---

## 🎨 Design & Aesthetic

Spendwise uses a bespoke dark-mode-first aesthetic with sleek slate backgrounds, vibrant emerald and cyan accents, and refined Material 3 typography designed for maximum readability, elegance, and visual delight.

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!
Feel free to check the [issues page](https://github.com/debojitdey037/SPENDWISE-APP/issues).

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👤 Author

**Debojit Dey**
- GitHub: [@debojitdey037](https://github.com/debojitdey037)
- Repository: [SPENDWISE-APP](https://github.com/debojitdey037/SPENDWISE-APP)
