<div align="center">

# 🌐 HubSpace

### Where teams connect, plan, and move together.

A production-grade, multi-tenant team collaboration app built with **Jetpack Compose** and **Material Design 3**.

<br/>

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Material 3](https://img.shields.io/badge/Material%20Design%203-757575?style=for-the-badge&logo=materialdesign&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)

![Status](https://img.shields.io/badge/status-active%20development-orange?style=flat-square)
![Platform](https://img.shields.io/badge/platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Architecture](https://img.shields.io/badge/architecture-MVVM%20%2B%20Clean-blueviolet?style=flat-square)
![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen?style=flat-square)

</div>

<br/>

> [!WARNING]
> ### 🚧 Currently in active development phase.
> Once production-ready, this badge and beta notices will be removed.

<br/>

## ✨ Overview

**HubSpace** is a production-grade, multi-tenant team collaboration and workspace application for Android. It is built entirely with **Jetpack Compose** and **Material Design 3**, and backed by a secure Firebase stack.

Under the hood, HubSpace follows an enterprise-grade architecture centered on a **Single Source of Truth (SSOT)** pattern through `AuthViewModel`. Combined with **zero-flicker pre-fetching**, the UI renders fully formed on first paint, with no layout shifts and no duplicate Firestore reads.

<br/>

## 🧰 Tech Stack

<table>
  <tr>
    <td><b>Language</b></td>
    <td>
      <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white" alt="Kotlin"/>
    </td>
  </tr>
  <tr>
    <td><b>UI Framework</b></td>
    <td>
      <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose"/>
      <img src="https://img.shields.io/badge/Material%20Design%203-757575?style=flat-square&logo=materialdesign&logoColor=white" alt="Material Design 3"/>
    </td>
  </tr>
  <tr>
    <td><b>Architecture</b></td>
    <td>
      <img src="https://img.shields.io/badge/MVVM-0A66C2?style=flat-square" alt="MVVM"/>
      <img src="https://img.shields.io/badge/Clean%20Architecture-2E7D32?style=flat-square" alt="Clean Architecture"/>
      <img src="https://img.shields.io/badge/Unidirectional%20Data%20Flow-E65100?style=flat-square" alt="UDF"/>
    </td>
  </tr>
  <tr>
    <td><b>Backend &amp; Database</b></td>
    <td>
      <img src="https://img.shields.io/badge/Firebase%20Auth-FFCA28?style=flat-square&logo=firebase&logoColor=black" alt="Firebase Authentication"/>
      <img src="https://img.shields.io/badge/Google%20Sign--In-4285F4?style=flat-square&logo=google&logoColor=white" alt="Google Sign-In"/>
      <img src="https://img.shields.io/badge/Cloud%20Firestore-FF6F00?style=flat-square&logo=firebase&logoColor=white" alt="Cloud Firestore"/>
    </td>
  </tr>
  <tr>
    <td><b>Notifications</b></td>
    <td>
      <img src="https://img.shields.io/badge/Firebase%20Cloud%20Messaging-FF9100?style=flat-square&logo=firebase&logoColor=white" alt="FCM"/>
    </td>
  </tr>
  <tr>
    <td><b>Dev Environment</b></td>
    <td>
      <img src="https://img.shields.io/badge/Ubuntu-E95420?style=flat-square&logo=ubuntu&logoColor=white" alt="Ubuntu"/>
      <img src="https://img.shields.io/badge/Android%20Studio-3DDC84?style=flat-square&logo=androidstudio&logoColor=white" alt="Android Studio"/>
      <img src="https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white" alt="Gradle"/>
      <img src="https://img.shields.io/badge/Git-F05032?style=flat-square&logo=git&logoColor=white" alt="Git"/>
    </td>
  </tr>
</table>

<br/>

## 🚀 Features

### ✅ Implemented

| | Feature | Description |
|---|---|---|
| 🚀 | **Zero-Flicker Architecture** | Pre-fetched ViewModel state loading that eliminates layout shifts and duplicate Firestore reads. |
| 🏢 | **Multi-Team Workspace** | Seamless team switching with reactive state synchronization and custom team selection sheets. |
| 📊 | **Smart Daily Brief** | `SummarizeDayCard` gives a context-aware workspace pulse that highlights active tasks, upcoming deadlines, and notices. |
| ⚡ | **Live GitHub Activity** | `GitHubLiveCard` fetches contribution stats, streaks, and repository counts in real time using community APIs and coroutine-safe networking. |
| 🔔 | **FCM Push Notifications** | Background and killed-state push handling through a custom `FirebaseMessagingService`. |
| 🎨 | **Modern Material 3 Design** | Dynamic surfaces, custom vector branding icons, and refined typography. |
| 🔐 | **Secure Access** | Google Sign-In with whitelist verification, backed by Firebase Authentication. |

<br/>

## 🏗️ Architecture

HubSpace uses **MVVM** with **Clean Architecture** principles and a strict **Unidirectional Data Flow**. `AuthViewModel` acts as the Single Source of Truth for session and workspace state.

```mermaid
flowchart LR
    UI["🎨 Compose UI<br/>(Material 3)"] -- "user events" --> VM["🧠 ViewModels<br/>(AuthViewModel = SSOT)"]
    VM -- "immutable UI state" --> UI
    VM --> REPO["📦 Repositories"]
    REPO --> AUTH["🔐 Firebase Auth<br/>+ Whitelist check"]
    REPO --> FS[("🔥 Cloud Firestore")]
    REPO --> GH["⚡ GitHub Community API"]
    FCM["🔔 FCM Service"] --> VM
```

**Key principles**

- **Single Source of Truth:** one authoritative state holder, so screens never disagree with each other.
- **Unidirectional Data Flow:** events flow up, state flows down, which keeps behavior predictable and testable.
- **Pre-fetching:** data is loaded before the UI needs it, which removes flicker and redundant reads.

<br/>

## 🗺️ Roadmap

- [ ] 📋 **Dedicated Tasks Board:** full-fledged checkable team task lists with strike-through states and quick-add bottom sheets.
- [ ] 🔑 **Team Join by Hash Code:** 8-character invite code generation and validation for instant workspace onboarding.
- [ ] 🔗 **Notification Deep Linking:** direct intent routing from background push notifications to specific team announcements.
- [ ] 🤖 **Edge AI Task Parsing:** a natural language widget that parses and schedules events and tasks from plain text.

<br/>

## 📸 Screenshots

<!-- Replace the placeholders below with real screenshots, e.g. docs/screenshots/home.png -->

<div align="center">

| Home | Team Switcher | Daily Brief | GitHub Activity |
|:---:|:---:|:---:|:---:|
| *coming soon* | *coming soon* | *coming soon* | *coming soon* |

</div>

<br/>

## ⚙️ Getting Started

### Prerequisites

- **Android Studio** (latest stable)
- **JDK 17** or newer
- A **Firebase project** with Authentication, Firestore, and Cloud Messaging enabled

### Setup

```bash
# 1. Clone the repository
git clone https://github.com/<your-username>/HubSpace.git
cd HubSpace

# 2. Open in Android Studio and let Gradle sync
```

1. In the [Firebase Console](https://console.firebase.google.com/), create a project and add an Android app with the project's package name.
2. Enable **Google** as a sign-in provider under **Authentication**.
3. Add your debug and release **SHA-1 / SHA-256** fingerprints to the Firebase app settings.
4. Download `google-services.json` and place it in the `app/` directory.
5. Set up the **Firestore** whitelist data used for sign-in verification.
6. Build and run on a device or emulator.

```bash
./gradlew assembleDebug
```

> [!NOTE]
> `google-services.json` contains project-specific configuration. Keep it out of public version control by adding it to `.gitignore`.

<br/>

## 🤝 Contributing

Contributions, issues, and feature requests are welcome.

1. Fork the project
2. Create your feature branch: `git checkout -b feature/amazing-feature`
3. Commit your changes: `git commit -m "Add amazing feature"`
4. Push to the branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

<br/>

## 📄 License

Distributed under the **MIT License**. See `LICENSE` for details.

<br/>

<div align="center">

**Built with ❤️ using Kotlin and Jetpack Compose**

⭐ If you like this project, consider giving it a star.

</div>