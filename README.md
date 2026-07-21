# Pan

**Pan** is a native Android companion app for university students, built entirely with Kotlin and Jetpack Compose. It bundles the everyday tasks a student juggles — tracking degree progress, finding classrooms, reading announcements, and browsing the course catalogue — into a single, offline-first app.

The bundled data targets the **Department of Informatics at the Athens University of Economics & Business (AUEB)** (courses, schedules, building maps, and the 3D campus tour), but the architecture is generic and can be repointed at another department by swapping the JSON assets.

> The UI is primarily in Greek.

## Features

| Feature | Description |
| --- | --- |
| 🔐 **Accounts** | Local registration & login with SHA-256 password hashing, unique email/username enforcement, and an optional "remember me" session. |
| 🏠 **Dashboard** | Home hub with quick access to every tool and a "next class" summary derived from the imported schedule. |
| 🎓 **DiplomaPal** | Degree-progress tracker. Check off completed courses to see acquired ECTS, remaining mandatory/elective requirements, a progress bar, and graduation eligibility (240 ECTS). |
| 📖 **Study Guide** | Searchable course catalogue with semester, ECTS, instructor, description, and prerequisites for each course. |
| 📷 **Classroom Scanner** | Point the camera at a classroom sign — on-device OCR (ML Kit) reads the room ID and cross-references the weekly schedule to tell you what class is (or isn't) in that room, including any lesson currently in progress. |
| 🗺️ **Class Locator** | Navigate the Patision building via an interactive floor-plan, or take a **3D virtual tour** of the campus in an embedded WebView. |
| 🔔 **Notifications** | Feed of course announcements. |
| 👤 **Profile** | View and edit account details; change password. |

### Greek-aware OCR

The Classroom Scanner has to read Greek room signs (`Αμφιθέατρο Α`, `Δ24`, `Α31`, …) with a text recognizer that is strongest on Latin script. [`ClassroomMatcher`](app/src/main/java/com/example/pan/util/ClassroomMatcher.kt) compensates by mapping Latin homoglyphs back to Greek (`A → Α`, `B → Β`, …), fuzzy-matching amphitheatre names when characters like `Φ`/`Θ` are dropped, and normalizing common digit misreads. [`ScheduleChecker`](app/src/main/java/com/example/pan/util/ScheduleChecker.kt) then resolves the canonical room ID against the weekly schedule.

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3, edge-to-edge, Compose Navigation
- **Architecture:** MVVM (`ViewModel` per screen, `AndroidViewModel` for those needing app context)
- **Persistence:** [Room](https://developer.android.com/training/data-storage/room) (users, completed courses) + `SharedPreferences` for session state
- **Camera & OCR:** CameraX + [ML Kit Text Recognition](https://developers.google.com/ml-kit/vision/text-recognition)
- **Bundled data:** JSON assets in [`app/src/main/assets/`](app/src/main/assets/) (`courses.json`, `classes.json`, `study_guide.json`, `notifications.json`)
- **Build:** Gradle (Kotlin DSL) with a version catalog and KSP


## Getting Started

### Requirements

- Android Studio (recent stable)
- JDK 11+
- Android SDK — `minSdk 24`, `compileSdk`/`targetSdk 36`
- A device or emulator; a physical device with a camera is recommended for the Classroom Scanner

### Build & Run

```bash
git clone <repo-url>
cd Pan

# Build a debug APK
./gradlew assembleDebug

# Or install straight to a connected device/emulator
./gradlew installDebug
```

Or open the project in Android Studio and hit **Run**.

### Tests

```bash
./gradlew test                 # JVM unit tests (e.g. ScheduleCheckerTest)
./gradlew connectedAndroidTest # Instrumented tests (device/emulator required)
```

## Permissions

- **Camera** — required by the Classroom Scanner (feature is optional; the camera is not marked as required hardware).
- **Internet** — used by the 3D virtual campus tour WebView.

## Notes

- All user data (accounts, completed courses, session) is stored **locally** on the device; there is no backend.
- Passwords are never stored in plaintext — only their SHA-256 hash is persisted.
- To adapt Pan to another department, replace the JSON files in `app/src/main/assets/` and update the building maps / tour URL.
</content>
</invoke>
