# HabitTracker App – Android

A comprehensive, native Android application developed for my **University Year 2, Semester 2** project. This app is designed to help users build consistency and improve their daily routines through goal tracking, mood monitoring, and automated reminders.

---

## 🚀 Features
* **Habit Management:** Create and track daily habits to build long-term consistency.
* **Mood Tracking:** Log daily moods to visualize the correlation between habits and mental well-being.
* **Progress Analytics:** Dedicated fragments to monitor your streaks and completion rates.
* **Onboarding Flow:** A custom introductory experience for new users to explain key app features.
* **Hydration Reminders:** Background notifications via `WorkManager` to ensure users stay hydrated.
* **Settings Customization:** Manage app preferences and user profile data.

---

## 🛠 Tech Stack & Architecture
This project implements modern Android development standards:

* **Language:** Kotlin
* **Architecture:** MVVM (Model-View-ViewModel) for clean code separation.
* **Asynchronous Tasks:** Kotlin Coroutines & Flow for smooth UI performance.
* **Background Tasks:** `WorkManager` for reliable scheduled notifications (e.g., HydrationWorker).
* **Local Persistence:** Room Database / SQLite for secure on-device data storage.
* **Navigation:** Android Jetpack Navigation component for seamless fragment transitions.



---

## 📂 Project Structure
The source code is organized into logical modules:
* `adapters/`: Manages the binding of habit and mood data to UI lists.
* `models/`: Data entities for Habits, Moods, and User profiles.
* `workers/`: Contains `HydrationWorker.kt` for background task execution.
* `fragments/`: Modular UI controllers (Home, Habits, Mood, Progress, Settings, Onboarding).
* `utils/`: Helper classes for date formatting and data validation.

---

## ⚙️ Installation
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/Aathi125/HabitTracker-App--Android.git](https://github.com/Aathi125/HabitTracker-App--Android.git)
    ```
2.  **Open in Android Studio:** Open the existing project and wait for Gradle sync to complete.
3.  **Run:** Select an emulator or physical device (API 24+) and click the **Run** icon.

---

## 🎓 Academic Context
* **Level:** University Year 2, Semester 2
* **Focus:** Mobile Application Development, UI/UX Design, Local Data Persistence.

---
Developed by **Aathi125**
