# DogFeed — Random Dog Gallery

> MIP-2 · Android AI-Assisted Development · DAM ISEL 2025/26  
> Student: A51388

---

## Description

**DogFeed** is a TikTok-style Android application that displays random dog images from the Dog CEO API in a vertical, full-screen scrolling feed.

Users can navigate through various dog breeds by swiping up and down.

---

## API Used

**Dog CEO API** — [dog.ceo/dog-api/](https://dog.ceo/dog-api/)

| Field | Value |
|---|---|
| Endpoint | `GET https://dog.ceo/api/breeds/image/random/10` |
| Auth | None (Public) |
| Format | JSON |

---

## Architecture

```
UI (Activity + XML)
      ↓
 ViewModel  (LiveData)
      ↓
 Repository
      ↓
 API Service (Retrofit + Gson)
```

Strict MVVM pattern — no business logic in Activity.

---

## Features

- Full-screen vertical feed (ViewPager2, TikTok-style)
- Fetch dog images from Dog CEO API via Retrofit
- Glide for async image loading with crossfade
- Breed name extraction from URL
- ProgressBar per item + global
- Error handling
- Swipe-to-refresh

---

## How to Run

### Prerequisites
- Android Studio
- Device/Emulator with Android 7.0+ (API 24)

### Execution
1. Open the project in Android Studio.
2. Run `./gradlew assembleDebug`.
3. Deploy the generated APK or run via Android Studio ▶.

---

## Project Structure

```
app/
  src/main/
    java/damA51388/galeriaaleatoria/
      MainActivity.kt
      adapter/ImageFeedAdapter.kt
      model/ImageItem.kt
      network/DogApiService.kt
      repository/ImageRepository.kt
      viewmodel/ImageViewModel.kt
    res/
      layout/activity_main.xml
      layout/item_image_card.xml
docs/
  01_overview.md ... 09_feature_extensions.md
agents.md
README.md
```