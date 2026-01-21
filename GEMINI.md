# QuizEditor Project Context

## Project Overview
**QuizEditor** is a modular Android application designed for creating and managing quizzes. It follows a multi-module architecture to separate concerns and improve build times. The project uses modern Android development practices, including Kotlin, Hilt for dependency injection, and Firebase for backend services.

**Key Technologies:**
*   **Language:** Kotlin
*   **UI Toolkit:** XML-based Layouts with View Binding, Material Components
*   **Dependency Injection:** Hilt
*   **Asynchronous Processing:** Kotlin Coroutines & Flow
*   **Backend:** Firebase (Authentication, Firestore)
*   **Navigation:** Android Navigation Component
*   **Build System:** Gradle (Kotlin DSL) with `buildSrc` for dependency management

## Architecture & Modules
The project is structured into feature and core modules:

*   **`app`**: The main application module, wiring everything together. Contains the `MainActivity` and global configuration.
*   **`core`**: Contains shared resources, extensions, and base classes used across feature modules.
*   **`auth`**: Handles user authentication (likely Firebase Auth).
*   **`firestore`**: Manages interaction with Cloud Firestore.
*   **`database_management`**: Dedicated module for database operations (possibly admin or data sync features).
*   **`home`**: The main landing screen/feature after login.
*   **`login_screen`**: UI and logic for the login flow.
*   **`quiz_mode`**: Feature module for taking or editing quizzes.
*   **`swipe_mode`**: Feature module implementing a swipe-based interface (Tinder-like card interface likely).
*   **`chat`**: Chat feature implementation.
*   **`buildSrc`**: Contains build logic, version catalogs (`Versions.kt`), and dependency definitions (`Dependencies.kt`).

## Architecture Guidelines (MVP)
The project follows the **Model-View-Presenter (MVP)** pattern. Use `SwipeQuestionDetailsFragment` (and its related files) as a reference implementation.

### Key Components

1.  **Contract (`SomeFeatureContract.kt`)**:
    *   Defines the agreement between View and Presenter.
    *   Must contain two nested interfaces: `View` (extends `BaseContract.View`) and `Presenter` (extends `BaseContract.Presenter<View>`).
    *   **View Interface:** Defines passive UI methods (e.g., `displayData`, `showError`).
    *   **Presenter Interface:** Defines user actions (e.g., `onButtonClicked`, `loadData`).

2.  **View (`SomeFeatureFragment.kt`)**:
    *   Extends `BaseFragment` or `BaseBottomSheetFragment`.
    *   Implements the `Contract.View` interface.
    *   Uses **View Binding** to access UI elements.
    *   **Responsibility:** Captures user input and immediately delegates it to the Presenter. Handles purely UI logic (animations, toasts, navigation) when instructed by the Presenter.
    *   **Passive:** Does not make business decisions or access the repository directly.

3.  **Presenter (`SomeFeaturePresenter.kt`)**:
    *   Extends `BasePresenter<Contract.View>`.
    *   Implements the `Contract.Presenter` interface.
    *   **Responsibility:** Holds state, executes business logic, and interacts with the Repository.
    *   **Coroutines:** Uses `presenterScope` for asynchronous operations.
    *   **Context-Free:** Does NOT hold `Context`. Uses `ResourceProvider` to access string resources if needed.

4.  **Repository (Data Layer)**:
    *   The Presenter interacts with a Repository interface to fetch/save data.
    *   Returns data wrapped in a `Response<T>` (Success, Error, Loading).

### Data Flow
1.  **User Action:** User interacts with the Fragment (View).
2.  **Delegation:** Fragment calls `presenter.someAction()`.
3.  **Processing:** Presenter validates logic and calls `repository.fetchData()`.
4.  **Result:** Repository returns a `Flow<Response<T>>`.
5.  **UI Update:** Presenter collects the result and calls `view.displayData(data)` or `view.displayError(message)`.
6.  **Rendering:** Fragment updates the UI views.

## Building & Running
The project uses the standard Gradle wrapper.

### Commands
*   **Build Debug APK:**
    ```bash
    ./gradlew assembleDebug
    ```
*   **Install Debug APK:**
    ```bash
    ./gradlew installDebug
    ```
*   **Run Unit Tests:**
    ```bash
    ./gradlew test
    ```
*   **Run Instrumented Tests:**
    ```bash
    ./gradlew connectedAndroidTest
    ```

## Development Conventions
*   **Dependency Management:** All dependencies and versions are defined in `buildSrc/src/main/kotlin/Dependencies.kt` and `Versions.kt`. Do not hardcode versions in individual module `build.gradle.kts` files.
*   **View Binding:** The project uses View Binding (`buildFeatures { viewBinding = true }`). XML layouts are the primary way of defining UI.
*   **Java/Kotlin Version:** The project is configured for JVM Target 11.
*   **Testing:** Uses JUnit 4, Mockk, and Turbine for unit testing. Espresso is available for UI testing.

## Key Files
*   `settings.gradle.kts`: Defines the project module structure.
*   `buildSrc/src/main/kotlin/Dependencies.kt`: Central place for library dependencies.
*   `app/src/main/AndroidManifest.xml`: App manifest, defining activities and permissions.
*   `app/src/main/java/com/rafalskrzypczyk/quizeditor/QuizEditorApplication.kt`: Application entry point (likely sets up Hilt).

## AI Agent Guidelines
1.  **Git Management:** Always add all new and edited files to Git after completing a task or sub-task.
2.  **Verification:** After every major step, build the project (e.g., `./gradlew assembleDebug`) and resolve any errors or warnings that appear.
3.  **Code Style:** Adding comments in the code is strictly forbidden. The code should be self-documenting.
4.  **Major Changes:** If a solution requires major changes to existing code or architecture, stop the task, present the proposed solution, and ask for explicit permission before proceeding.
5.  **Structural Consistency:** Strictly follow the current project structure and patterns (like MVP and modularization) when implementing new functionalities.
