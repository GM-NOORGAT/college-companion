COLLEGE COMPANION — NATIVE ANDROID APP SOURCE

This is a native Android project written in Java (not a website and not Kotlin).
Sections: Home, Timetable, Notices, Assignments, Events.
Timetable, Notices, Assignments and Events support adding, editing and deleting entries; assignments also support marking complete/pending. Data is saved locally on the device.

IMPORTANT: This ZIP is source code, NOT a prebuilt APK. Android Studio is not required on your laptop if you use the included cloud-build workflow, but you will need to upload these files to a GitHub repository and run the workflow online.

Cloud build: upload the extracted project files to a GitHub repository. The workflow at .github/workflows/build-apk.yml runs Gradle and builds the APK. Open the repository's Actions tab, run "Build College Companion APK", then download the artifact named College-Companion-debug-apk.
