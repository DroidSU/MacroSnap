# 📸 MacroSnap

**MacroSnap** is a modern, AI-powered nutrition tracking application designed to simplify healthy eating. By leveraging Google's Gemini AI, the app allows users to snap a photo of their meal and instantly receive a detailed nutritional breakdown, including calories and macronutrients.

---

## 🚀 Key Features

*   **🤖 AI Meal Analysis**: Integration with Google's **Gemini 2.5 Flash** model to identify dishes and estimate calories, protein, carbs, and fats from a single image.
*   **📊 Weekly Dashboard**: A comprehensive summary of weekly intake, helping users stay on track with their nutritional goals.
*   **📂 Meal History**: A dedicated log of all scanned meals with sorting options (Date, Alphabetical) and local persistence.
*   **🔐 Secure Authentication**: Implements **Firebase Authentication** coupled with the modern **Android Credential Manager** for a seamless and secure sign-in experience.
*   **🎨 Modern UI/UX**: Built entirely with **Jetpack Compose** and **Material 3**, featuring a sleek "3D" aesthetic, smooth transitions, and Lottie animations.

---

## 🛠 Technical Stack

### **Core**
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Dependency Injection**: Manual injection via ViewModel Factories

### **Libraries & APIs**
- **AI/ML**: Google Generative AI SDK (Gemini API)
- **Camera**: CameraX (for high-performance image capture)
- **Database**: Room Persistence Library (Local SQLite caching)
- **Image Loading**: Coil (Async image loading for meal logs)
- **Animations**: Lottie for Compose
- **Networking**: Kotlin Serialization & Coroutines

---

## 🏗 Architecture Overview

MacroSnap follows the **Clean Architecture** principles to ensure scalability and maintainability:

1.  **Data Layer**: 
    - `MealRepository` & `AuthRepository` act as the single source of truth.
    - `MealDao` for local persistence.
    - `GeminiService` for remote AI analysis.
2.  **Domain Layer**: Clean data models (`MealAnalysis`, `MealEntity`).
3.  **UI Layer**:
    - **ViewModels**: State management using `StateFlow` and `collectAsStateWithLifecycle`.
    - **Composables**: Modular and reusable UI components.

---

## ⚙️ Technical Highlights

### **AI Prompt Engineering**
The app utilizes structured prompt engineering to ensure the Gemini model returns data in a strictly valid JSON format. This allows the application to deserialize AI responses directly into Kotlin data classes using `kotlinx.serialization`.

### **CameraX Integration**
Custom implementation of the CameraX lifecycle-aware component to capture high-resolution bitmaps which are then optimized/compressed before being sent for AI analysis, ensuring low latency and reduced data usage.

### **Modern Auth Flow**
MacroSnap adopts the latest **Credential Manager API**, providing a unified sign-in interface that supports Passkeys, Google Sign-In, and traditional passwords, future-proofing the security layer.

---

## 🛠 Installation & Setup

1.  Clone the repository:
    ```bash
    git clone https://github.com/yourusername/MacroSnap.git
    ```
2.  Obtain a **Gemini API Key** from [Google AI Studio](https://aistudio.google.com/).
3.  Add your API key to the `local.properties` file:
    ```properties
    GEMINI_API_KEY=your_api_key_here
    ```
4.  Build and run the project using Android Studio Ladybug or later.

---

## 👨‍💻 Author

**Sujoy Dutta** -

*Project developed as a showcase of modern Android development practices, AI integration, and high-quality UI design.*
