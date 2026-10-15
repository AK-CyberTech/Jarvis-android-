# JARVIS Android Assistant — starter project

A native Android wrapper with a futuristic local dashboard, Android speech-recognition intent, spoken responses (TextToSpeech), supported app shortcuts, web search, and alarm intent.

## Important
This is a starter prototype, not a full autonomous assistant. It does not include an AI API key or cloud AI brain. Never put API keys in client-side code. Hands-free always-listening, arbitrary control of other apps, messaging automation, and many system settings require additional design, permissions, and Android-specific restrictions.

## Build an APK using only your Android phone
This repository includes a GitHub Actions workflow. You can upload these files to a GitHub repository from your phone browser, then let GitHub build the APK in the cloud.

1. On your phone, open GitHub and create a new repository named `jarvis-android`.
2. Extract this ZIP using your phone's file manager or an archive app.
3. Upload the extracted project files and folders to the repository (not the ZIP itself). Ensure `.github/workflows/android.yml` is included.
4. Open the repository's **Actions** tab and enable Actions if prompted.
5. Run **Build JARVIS APK** using **Run workflow**, or push a commit to `main`.
6. Open the completed workflow run and download the `jarvis-debug-apk` artifact.
7. Extract the artifact ZIP, then tap the APK to install. Android may ask you to allow installs from that browser/file manager. Only allow this for the trusted file you built yourself.

GitHub's web interface can be awkward for uploading many files on mobile. If the upload flow is difficult, use a browser-based Git repository editor or cloud development environment. Never share your GitHub password or access token with anyone.

## Build locally
Open this folder in Android Studio on a computer and build the debug APK. Gradle wrapper files are not included in this starter ZIP; Android Studio can generate/supply the wrapper when importing the project.

## Commands
- Open YouTube, WhatsApp, Chrome, Camera, Maps, Settings
- Search for a phrase
- Tell time/date
- Set alarm for a time
- Tell a simple joke

## Privacy
Voice recognition is launched through Android's speech-recognition service. Depending on the device and provider, speech may be processed by that service. Microphone permission is requested at runtime. This app does not contain analytics or an embedded AI API key.
