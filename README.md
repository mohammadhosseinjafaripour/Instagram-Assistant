# Instagram Assistant 📲

Instagram Assistant is an Android app that helps you download Instagram content
quickly using links or usernames. It includes manual download tools and an
automatic clipboard listener that can grab content as soon as you copy a link.

> ⚠️ **Disclaimer**: This project is for educational purposes only. Downloading
> content may violate Instagram's terms of service or local laws. Please respect
> creators' rights and only download content you have permission to use.

## ✨ Features

- 👤 **Profile tools**: fetch profile picture and view/copy biography
- 📸 **Post download**: download images and videos from a post/reel link
- 📖 **Story download**: fetch and download all stories by username
- ⚡ **Auto mode**: monitor clipboard and auto-download Instagram links
- 📋 **Clipboard helpers**: paste links/usernames from clipboard
- 🧭 **Guided UI**: built-in guide for using the app

## 🧩 What the app can do

| Area | Capability | Input | Output |
| --- | --- | --- | --- |
| Profile | Fetch profile photo | Username | HD profile image |
| Profile | View biography | Username | Bio text |
| Posts | Download images/videos | Instagram post URL | Media files |
| Stories | Download story media | Username | Story images/videos |
| Auto Mode | Clipboard listener | Copied Instagram link | Auto download |
| Tools | Open Instagram app | Tap from home | Launch Instagram |

## 🏗️ Project overview

The app uses a tab-based UI with separate screens for Profile, Story, and
Image/Video downloads, plus an Auto mode. Downloads are performed by a background
`IntentService` and status updates are shown through a `ResultReceiver`.

Auto mode runs a foreground service that listens for clipboard changes. When an
Instagram link is detected, it hits a lightweight API endpoint to resolve the
media URL and starts a download immediately.

## 🧪 Tech stack

- **Language**: Java
- **UI**: AndroidX + Material + MeowBottomNavigation
- **Networking**: Volley
- **Media**: Glide
- **UX**: Toasty, LoadingButton

## 🔧 Setup

1. Open the project in Android Studio.
2. Sync Gradle and build the project.
3. Run on a device or emulator (Android 5.0+).

> The app expects a parse key in `app/src/main/res/values/secrets.xml` and uses
> API endpoints like:
> - `https://programchi.ir/InstaApi/instagram.php`
> - `https://programchi.ir/InstaApi/story.php`

## 🔐 Permissions

- `INTERNET` and `ACCESS_NETWORK_STATE` for API calls
- `READ/WRITE_EXTERNAL_STORAGE` to save downloaded media
- `FOREGROUND_SERVICE` for auto-download mode

## 📁 Download location

Media files are saved to:

- `InstaAssistant/Images`
- `InstaAssistant/Videos`
- `InstaAssistant/Story/Images`
- `InstaAssistant/Story/Videos`
- `InstaAssistant/Profile`

## 🚀 Usage tips

- Paste a link or username using the clipboard button.
- Turn on **Auto Mode** to download when you copy Instagram links.
- Use **Download All** to save all items in a carousel or story list.

## 🤝 Contributing

PRs are welcome. If you plan major changes, open an issue first to discuss ideas.

## 📄 License

Add a license file if you plan to open-source this project.
