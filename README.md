# सब्दरूप (Sabdaroopa)

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Language](https://img.shields.io/badge/Language-Kotlin-blue.svg)](https://kotlinlang.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.0.0-orange.svg)](https://github.com/nxzef/sabdaroopa/releases)

> Your offline companion for learning Sanskrit grammar

An Android application designed to simplify Sanskrit declension table learning by providing structured, interactive, and offline access to Sanskrit word declensions. Built as an academic project for BA Sanskrit at University of Calicut.

---

## 📱 Screenshots

<!-- TODO: Add screenshots here -->
<!-- Suggested screenshots to include:
1. Home Screen - showing category list
2. Category Screen - showing word list with filters
3. Table Screen - showing declension table
4. Quiz Home Screen - showing quiz options
5. Settings Screen - showing preferences
6. Dark Theme Example - showing app in dark mode
-->

```
[Home Screen]  [Word List]  [Declension Table]  [Quiz]  [Settings]
```

*Add screenshots in a `/screenshots` folder and link them here*

---

## ✨ Features

- **📚 130+ Sanskrit Words** - Complete declension tables organized by categories
- **🔍 Advanced Search** - Search by meaning, Devanagari script, or IAST transliteration
- **📱 100% Offline** - No internet required, all data stored locally
- **🎯 Interactive Quiz Mode** - Test your knowledge with built-in quizzes
- **⭐ Favorites** - Save frequently used words for quick access
- **🎨 Material Design 3** - Modern, beautiful user interface
- **🌓 Theme Support** - Light and Dark themes with dynamic colors (Android 12+)
- **⚙️ Customizable** - Adjustable font sizes, vibration feedback, and preferences
- **🗂️ Organized Categories**
  - साधारण शब्द विभागः (General Words)
  - विशेष शब्द विभागः (Specific Words)
  - सर्वनाम शब्दप्रकरणम् (Pronouns)
- **🔤 Filters** - Filter by vowel/consonant endings and gender (पुल्लिङ्गः, स्त्रीलिङ्गः, नपुंसकलिङ्गः)

---

## 🎯 Motivation

This app was born from a unique vision during my final year as a BA Sanskrit student at SNGS College, Pattambi (2022-2025). While peers wrote traditional books on Mahabharata and Ramayana, I chose to do something different—something that had never been done in our college before: **build an Android app**.

Traditional Sanskrit grammar resources are limited to printed books and PDFs, making it difficult to quickly find and analyze word declensions. Sabdaroopa bridges this gap by providing an interactive, searchable, and structured digital platform for Sanskrit learners.

---

## 🏗️ Architecture

This project follows **Clean Architecture** principles with **MVVM** (Model-View-ViewModel) pattern:

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│    (Jetpack Compose + ViewModels)   │
└─────────────┬───────────────────────┘
              │
┌─────────────▼───────────────────────┐
│          Domain Layer               │
│  (Use Cases, Business Logic, etc.)  │
└─────────────┬───────────────────────┘
              │
┌─────────────▼───────────────────────┐
│           Data Layer                │
│  (Room Database + Repositories)     │
└─────────────────────────────────────┘
```

### Key Components:

- **UI Layer**: Jetpack Compose with Material Design 3
- **ViewModel**: State management with StateFlow
- **Repository**: Data abstraction layer
- **Room Database**: Pre-packaged SQLite database with 130+ words
- **Dependency Injection**: Hilt for clean dependency management

---

## 🛠️ Tech Stack

| Component | Technology |
|-----------|-----------|
| **Language** | [Kotlin](https://kotlinlang.org/) |
| **UI Framework** | [Jetpack Compose](https://developer.android.com/jetpack/compose) |
| **Architecture** | MVVM + Clean Architecture |
| **Dependency Injection** | [Hilt](https://dagger.dev/hilt/) |
| **Database** | [Room](https://developer.android.com/training/data-storage/room) (SQLite) |
| **Async** | Kotlin Coroutines + Flow |
| **Navigation** | Jetpack Navigation Compose |
| **Design System** | Material Design 3 |
| **Data Storage** | DataStore (Preferences) |

---

## 📦 Project Structure

```
com.nxzef.sabdaroopa/
├── data/
│   ├── local/          # Room database, DAOs
│   ├── model/          # Data models
│   └── repository/     # Repository implementations
├── di/                 # Hilt modules
├── domain/
│   ├── manager/        # App-wide managers (HapticManager, FocusManager)
│   └── platform/       # Platform capabilities
├── ui/
│   ├── component/      # Reusable UI components
│   ├── screen/         # Feature screens
│   │   ├── home/
│   │   ├── table/
│   │   ├── favorites/
│   │   ├── quiz/
│   │   ├── settings/
│   │   └── about/
│   └── theme/          # Theme, colors, typography
└── utils/              # Extension functions, helpers
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17 or higher
- Android SDK (Min SDK: 24, Target SDK: 34)
- Kotlin 1.9.0 or higher

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/nxzef/sabdaroopa.git
   cd sabdaroopa
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory

3. **Build the project**
   ```bash
   ./gradlew build
   ```

4. **Run on device/emulator**
   - Connect your Android device or start an emulator
   - Click "Run" in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

---

## 📊 Database Schema

The app uses a pre-packaged Room database containing Sanskrit word declensions:

```kotlin
@Entity(tableName = "sabda_table")
data class Sabda(
    @PrimaryKey val id: Int,
    val word: String,           // Sanskrit word
    val meaning: String,        // English meaning
    val translit: String,       // IAST transliteration
    val anta: String,           // Ending sound
    val category: Category,     // Word category
    val gender: Gender,         // Grammatical gender
    val sound: Sound,           // Vowel/Consonant
    val declension: Declension  // Full declension table
)
```

**Declension Structure**: 8 cases (विभक्ति) × 3 numbers (वचन) = 24 forms per word

---

## 🎨 Design Decisions

### Why Jetpack Compose?
- Modern declarative UI
- Less boilerplate than XML
- Better state management
- Smooth animations out-of-the-box

### Why Room Database?
- Offline-first approach
- Type-safe queries
- Efficient data storage
- Pre-packaged database support

### Why MVVM?
- Clear separation of concerns
- Testable business logic
- Lifecycle-aware components
- Easy state management

---

## 🤝 Contributing

Contributions are welcome! If you'd like to contribute:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Contribution Guidelines

- Follow Kotlin coding conventions
- Write meaningful commit messages
- Add comments for complex logic
- Update documentation if needed
- Test thoroughly before submitting PR

---

## 📝 Development Journey

**Timeline**: December 2024 - June 2025 (6-7 months)

### Challenges Faced:
- ⏰ Late nights while balancing academics
- 🏥 Health struggles during development
- 🌙 Coding during Ramadan while fasting
- 📚 Learning Kotlin & Jetpack Compose from scratch
- 🎨 Designing custom declension table layouts
- 💾 Database migration and optimization

### My Programming Journey:
```
10th Standard → HTML/CSS → JavaScript → React → React Native → Flutter → Kotlin
```

This is my **first complete Android app**, built entirely while learning!

---

## 📚 Data Sources

The declension data was manually extracted from:

- **Sabda Manjari** (Primary reference book)
- [My Coaching](https://example.com) - Sanskrit grammar lessons
- [Learn Sanskrit](https://example.com) - Grammar resources  
- [Sanskrit Abhyas](https://example.com) - Practice materials

<!-- TODO: Replace example.com with actual URLs if available -->

---

## 🎓 Academic Context

**Project Type**: Final Year Academic Project  
**Degree**: BA Sanskrit (2022-2025)  
**College**: Sree Neelakanta Government Sanskrit College (SNGS), Pattambi  
**University**: University of Calicut  
**Guide**: Dr. Rajalakshmy M (Associate Professor, Dept. of Sanskrit)  
**HOD**: Dr. A. Vasu (Head of Department, Sanskrit)

---

## 🙏 Acknowledgments

### Project Guide
**Dr. Rajalakshmy M**  
*Associate Professor, Dept. of Sanskrit, SNGS College, Pattambi*  
For invaluable guidance, continuous support, and encouragement throughout development.

### Head of Department
**Dr. A. Vasu**  
*Head of Department, Sanskrit, SNGS College, Pattambi*  
For approving this innovative project and providing valuable suggestions.

### Special Thanks
- **Varsha** - For providing the 'Sabda Manjari' book
- **Gopi Krishnan** - For continuous support and encouragement
- **All friends** - Who encouraged this journey

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 Mohammed Naseef V M

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

<!-- TODO: Add full LICENSE file in project root -->

---

## 📞 Contact

**Mohammed Naseef V M**  
BA Sanskrit Student | Android Developer

- 📧 Email: [scriptgoat609@gmail.com](mailto:scriptgoat609@gmail.com)
- 💻 GitHub: [@nxzef](https://github.com/nxzef)
- 💼 LinkedIn: [nxzef](https://www.linkedin.com/in/nxzef/)

---

## 🌟 Future Enhancements

- [ ] Add verb conjugation tables (तिङन्त प्रकरणम्)
- [ ] Implement sandhi rules reference
- [ ] Add audio pronunciation for words
- [ ] Multi-language support (Malayalam, Hindi)
- [ ] Cloud sync for favorites and progress
- [ ] Expand word database (500+ words)
- [ ] Advanced quiz modes with scoring
- [ ] Export declension tables as PDF
- [ ] Widget support for quick access
- [ ] Wear OS companion app

---

## 📈 Project Stats

<!-- TODO: Update these stats -->
- **Lines of Code**: ~15,000+
- **Development Time**: 6-7 months
- **Commits**: XXX
- **Files**: XXX Kotlin files
- **Database Size**: 130 words, ~500KB
- **APK Size**: ~XX MB

---

## 🐛 Known Issues

None at the moment! If you find any bugs, please [open an issue](https://github.com/nxzef/sabdaroopa/issues).

---

## 🔖 Version History

### v1.0.0 (2025)
- ✨ Initial release
- 📚 130+ Sanskrit words with declensions
- 🎯 Quiz feature
- ⭐ Favorites functionality
- 🎨 Material Design 3 UI
- 🌓 Dark/Light themes
- ⚙️ Customizable preferences

---

## 💡 Inspiration

> "This app represents not just code, but a bridge between ancient Sanskrit wisdom and modern technology."

Sanskrit is one of the oldest languages in the world, and preserving its grammar through digital means ensures it remains accessible for future generations. This project demonstrates that traditional learning can be revolutionized through thoughtful application of modern technology.

---

## 🎯 Impact

This project:
- ✅ First Android app developed in SNGS College as an academic project
- ✅ Demonstrates the fusion of traditional Sanskrit studies with modern tech
- ✅ Provides free, accessible learning tool for Sanskrit students worldwide
- ✅ Encourages digital innovation in classical language education

---

<div align="center">

**If you find this project helpful, please ⭐ star the repository!**

Made with ❤️ by [Mohammed Naseef V M](https://github.com/nxzef)

*Academic Project | University of Calicut | 2025*

</div>
