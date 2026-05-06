# Kotlin Flows Showcase App 🚀

A polished, educational Android project built to explore and master **Kotlin Flows** using **Jetpack Compose** and the **MVVM architecture**. This application provides a hands-on, page-by-page visual comparison between **Cold Flows** and **Hot Flows** (`StateFlow` and `SharedFlow`).

---

## 📑 Project Structure

The project is structured under the `com.boshra.practice` package and centers around two main architectural layers:
1. **`MyViewModel`**: Manages business logic and encapsulates data production using various Flow primitives.
2. **`MainActivity` (UI Layer)**: Built entirely with Jetpack Compose, it subscribes to (consumes) the Flows and reflects changes reactively on the screen.

---

## 💡 Flow Architecture Deep Dive

The app utilizes a `HorizontalPager` split into three distinct pages, each dedicated to demonstrating a specific type of Kotlin Flow:

### 1. Cold Flows ❄️ (Page 1/3)
* **Concept:** Cold Flows do not emit data until a consumer actively starts collecting from them. Each new collector triggers a fresh, independent execution of the flow block.
* **Implementation:**
  * `countdownFlow`: A classic countdown timer starting from 100, emitting decremented values every 1 second.
  * `cryptoPriceFlow`: A real-time simulation of cryptocurrency market volatility, updating a base price randomly every 2 seconds.
* **UI Consumption:** Bound using `collectAsStateWithLifecycle()` to transform emissions into Compose-observable states safely synchronized with the Android lifecycle.

### 2. StateFlow (Hot Flow) 🔥 (Page 2/3)
* **Concept:** A Hot Flow designed specifically to represent **UI State**. It always retains and emits the **latest cached value** (`Latest Value`) immediately to any new subscriber, making it perfect for state preservation across configuration changes.
* **Implementation:**
  * `uiState`: Modeled using a sealed interface (`TasksUiState`). It initializes as `Loading`, and after a simulated 3-second network delay, it updates to `Success` with a list of dummy tasks.
* **UI Consumption:** Handled via an explicit `when` branch mapping. The UI reactively switches between a `CircularProgressIndicator` and a scrollable `LazyColumn` based on the active state.

### 3. SharedFlow (Hot Flow) ⚡ (Page 3/3)
* **Concept:** A Hot Flow tailored for **one-time design events** (Side-Effects) like displaying Snackbars, playing sounds, or triggering screen navigation. It broadcasts events to all active collectors but does *not* cache or replay events for late-comers by default (fire-and-forget).
* **Implementation:**
  * `uiEvent`: Orchestrates the OTP verification sequence (`OtpEvent`). Based on user input inside `verifyOtpCode()`, it emits distinct events: `ShowSnackbarMessage` for validation errors or `MapsToHomeScreen` upon entering the correct master code (`1234`).
* **UI Consumption:** Observed inside a `LaunchedEffect(key1 = true)` block. Since these are discrete transient actions rather than persistent states, they are collected directly as continuous stream events.

---

## 🛠️ Tech Stack & Libraries

* **Kotlin Coroutines & Flows**: Direct asynchronous streaming architecture (`Flow`, `MutableStateFlow`, `MutableSharedFlow`).
* **Jetpack Compose**: For building a 100% declarative and fluid user interface.
* **Android Architecture Components**:
  * `ViewModel`: Preserves flow states safely across configuration changes (e.g., screen rotations).
  * `Lifecycle Runtime Compose`: Ensures memory-safe collection using `collectAsStateWithLifecycle()`.
* **Material 3 UI**: Incorporates modern styling hooks like `Scaffold`, `HorizontalPager`, and `SnackbarHost`.

---

## 🏃‍♂️ App Experience Walkthrough

1. **First Slide (Cold Flow):** Observe the active timer and the fluctuation of the crypto price tick instantly upon entering the screen.
2. **Second Slide (StateFlow):** Slide right to view an asynchronous data fetch. A loader turns for exactly 3 seconds before smoothly populating a checklist of college tasks.
3. **Third Slide (SharedFlow):** Enter a random string into the OTP text field to trigger a diagnostic Material 3 Snackbar message, or type `1234` to simulate successful authentication.
