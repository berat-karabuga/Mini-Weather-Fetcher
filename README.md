<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:4FC3F7,100:0288D1&height=200&section=header&text=Mini%20Weather%20Fetcher&fontSize=50&fontColor=ffffff&fontAlignY=38&desc=Your%20city.%20Your%20sky.%20Real-time.&descAlignY=60&descColor=ffffff&animation=fadeIn" width="100%"/>

<br/>

[![Android CI](https://github.com/berat-karabuga/Mini-Weather-Fetcher/actions/workflows/android.yml/badge.svg?style=for-the-badge)](https://github.com/berat-karabuga/Mini-Weather-Fetcher/actions)&nbsp;
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)&nbsp;
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)&nbsp;
![Open-Meteo](https://img.shields.io/badge/Open--Meteo-Free%20API-FF6B35?style=for-the-badge)&nbsp;
![Room](https://img.shields.io/badge/Room-DB-3DDC84?style=for-the-badge&logo=android&logoColor=white)&nbsp;
![Min SDK](https://img.shields.io/badge/Min%20SDK-26-success?style=for-the-badge)

<br/>

```
☀️  Live weather  •  📊 24h forecasts  •  📅 7-day outlook  
⭐  Save favorites  •  ⚔️  Compare cities  •  📍 GPS-powered
```

</div>

---

<div align="center">

## ⚡ Why Mini Weather Fetcher?

</div>

```
🚀  No API key required          — Open-Meteo is completely free & open
📍  Auto GPS detection           — Opens straight to your location  
⚡  Debounced city search        — Fast, smart, no redundant API spam
🗄️  Room persistence             — Favorites survive app restarts
🎨  Weather-adaptive UI          — Gradients shift with live conditions
🔄  CI/CD via GitHub Actions     — Every commit is built & verified
```

---

## 📸 Screenshots

<div align="center">

<table>
  <tr>
    <td align="center" width="33%">
      <img src="screenshots/home_screen.png" width="220" style="border-radius: 20px"/><br/>
      <sub><b>🏠 Home Screen</b></sub><br/>
      <sub>GPS auto-detect · live weather<br/>hourly forecast · 7-day outlook</sub>
    </td>
    <td align="center" width="33%">
      <img src="screenshots/favorites_screen.png" width="220" style="border-radius: 20px"/><br/>
      <sub><b>⭐ Favorites Screen</b></sub><br/>
      <sub>City grid · weather-adaptive cards<br/>one-tap switch · persistent storage</sub>
    </td>
    <td align="center" width="33%">
      <img src="screenshots/compare_screen.png" width="220" style="border-radius: 20px"/><br/>
      <sub><b>⚔️ Compare Screen</b></sub><br/>
      <sub>Split-screen battle · live gradients<br/>humidity & wind side-by-side</sub>
    </td>
  </tr>
</table>

</div>

> 📁 Screenshots are located in the [`screenshots/`](./screenshots) folder of the repository.

---

## 🗺️ Architecture Overview

```mermaid
graph TB
    subgraph UI["🖼️  UI Layer — Jetpack Compose"]
        MS["MainScreen\nNavHost + BottomBar"]
        HS["HomeScreen"]
        FS["FavoritesScreen"]
        CS["CompareScreen"]
        MS --> HS & FS & CS
    end

    subgraph VM["🧠  ViewModel Layer"]
        HVM["HomeViewModel\nweather · search · favorites"]
        FVM["FavoritesViewModel\nfavorites list · weather cache"]
        CVM["CompareViewModel\ndual city A/B comparison"]
    end

    subgraph DATA["📡  Remote Data"]
        RC["RetrofitClient\nOkHttp + Logging"]
        API["Open-Meteo API\nweather + geocoding"]
        RC --> API
    end

    subgraph LOCAL["🗄️  Local Data — Room"]
        DB[("AppDatabase")]
        DAO["FavoriteDao\nFlow · insert · delete · exists"]
        ENT["FavoriteCity Entity\ncityName · lat · lon"]
        DB --> DAO --> ENT
    end

    HS -->|"StateFlow"| HVM
    FS -->|"StateFlow"| FVM
    CS -->|"StateFlow"| CVM

    HVM & FVM & CVM -->|"suspend calls"| RC
    HVM & FVM -->|"Flow queries"| DAO

    style UI fill:#1a237e,color:#fff,stroke:#3949AB
    style VM fill:#1b5e20,color:#fff,stroke:#388E3C
    style DATA fill:#b71c1c,color:#fff,stroke:#D32F2F
    style LOCAL fill:#e65100,color:#fff,stroke:#F57C00
```

---

## 🔄 App State Machine

```mermaid
stateDiagram-v2
    [*] --> AppLaunch

    AppLaunch --> CheckPermission

    CheckPermission --> RequestPermission : ❌ Not granted
    CheckPermission --> GetGPSLocation   : ✅ Granted

    RequestPermission --> GetGPSLocation  : User allows
    RequestPermission --> PermissionDenied : User denies

    GetGPSLocation --> GeocodeCity : Location found
    GetGPSLocation --> Fallback    : Location null
    Fallback --> FetchWeather      : Use default coords

    GeocodeCity --> FetchWeather   : City name resolved

    FetchWeather --> Loading
    Loading --> Success : ✅ API OK
    Loading --> Error   : ❌ Network fail

    Success --> CheckFavorite
    CheckFavorite --> ShowFilled  : ⭐ Is favorite
    CheckFavorite --> ShowOutline : 🤍 Not favorite

    Success --> CitySearch        : User types
    CitySearch --> Debounce       : 500ms delay
    Debounce --> GeocodingAPI     : query ≥ 2 chars
    GeocodingAPI --> ShowDropdown : Results returned
    ShowDropdown --> FetchWeather : City selected
```

---

## 🌐 API Request Flow

```mermaid
sequenceDiagram
    participant U  as 👤 User
    participant HS as HomeScreen
    participant VM as HomeViewModel
    participant GEO as Geocoding API
    participant WX  as Open-Meteo API
    participant DB  as Room DB

    U->>HS: Types city name
    HS->>VM: onSearchQueryChange(query)
    VM->>VM: delay(500ms) debounce ⏱️
    VM->>GEO: GET /v1/search?name=query&count=5
    GEO-->>VM: List<LocationResult>
    VM-->>HS: searchResults StateFlow updated

    U->>HS: Selects a city from dropdown
    HS->>VM: fetchWeather(lat, lon, name)
    VM->>WX: GET /v1/forecast?latitude=...&hourly=...&daily=...
    WX-->>VM: WeatherResponse (current + hourly + daily)
    VM->>DB: isFavorite(cityName)
    DB-->>VM: Boolean
    VM-->>HS: WeatherState.Success emitted ✅
```

---

## 📁 Project Structure

```
📦 com.stargazer.miniweatherfetcher
│
├── 📂 components/
│   ├── 🧩 CitySearchBar.kt        ← Debounced input + animated result dropdown
│   └── 🧩 WeatherCard.kt          ← Expandable card with weather-adaptive gradients
│
├── 📂 data/
│   ├── 🌐 RetrofitClient.kt       ← Singleton + OkHttp logging interceptor
│   ├── 🌐 WeatherApi.kt           ← Retrofit interface (weather + geocoding)
│   └── 📂 local/
│       ├── 🗄️ AppDatabase.kt      ← Room DB definition
│       ├── 🗄️ FavoriteCity.kt     ← @Entity (cityName PK, lat, lon)
│       └── 🗄️ FavoriteDao.kt      ← Flow queries, insert, delete, exists
│
├── 📂 model/
│   ├── 📋 WeatherResponse.kt      ← CurrentWeather + HourlyForecast + DailyForecast
│   └── 📋 GeocodingResponse.kt    ← LocationResult with coordinates
│
├── 📂 navigation/
│   ├── 🗺️ Routes.kt               ← @Serializable type-safe destinations
│   └── 🗺️ BottomNavItem.kt        ← Nav item wrapper model
│
├── 📂 screens/
│   ├── 🖥️ MainScreen.kt           ← Scaffold, shared ViewModel, NavHost
│   ├── 🖥️ HomeScreen.kt           ← GPS + search + hourly LazyRow + daily Card
│   ├── 🖥️ FavoritesScreen.kt      ← LazyVerticalGrid + PremiumFavoriteCards
│   └── 🖥️ CompareScreen.kt        ← Split-screen battle + animated VS button
│
├── 📂 utils/
│   └── 🛠️ WeatherUtils.kt         ← getWeatherEmoji() · getWeatherDescription() · formatDate()
│
├── 📂 viewmodel/
│   ├── 🧠 HomeViewModel.kt        ← WeatherState sealed class + toggleFavorite
│   ├── 🧠 FavoritesViewModel.kt   ← stateIn() + weatherMap lazy caching
│   └── 🧠 CompareViewModel.kt     ← A/B city state + parallel fetch + reset
│
└── 🚀 MainActivity.kt             ← DatabaseProvider.init() + enableEdgeToEdge()
```

---

## 🌤️ WMO Weather Code Reference

```mermaid
graph LR
    subgraph Clear["☀️ Clear"]
        C0["0 — Clear Sky"]
    end
    subgraph Clouds["⛅ Clouds"]
        C12["1–2 Partly Cloudy"]
        C3["3 — Overcast"]
    end
    subgraph Fog["🌫️ Atmosphere"]
        C45["45, 48 — Fog"]
    end
    subgraph Rain["🌧️ Rain"]
        C51["51–57 Drizzle"]
        C61["61–67 Rain"]
        C80["80–82 Showers"]
    end
    subgraph Snow["❄️ Snow"]
        C71["71–77 Snow"]
        C85["85–86 Snow Showers"]
    end
    subgraph Storm["⛈️ Storm"]
        C95["95–99 Thunderstorm"]
    end

    style Clear  fill:#FFF176,stroke:#F9A825,color:#333
    style Clouds fill:#B0BEC5,stroke:#607D8B,color:#111
    style Fog    fill:#ECEFF1,stroke:#90A4AE,color:#333
    style Rain   fill:#1565C0,stroke:#0D47A1,color:#fff
    style Snow   fill:#E3F2FD,stroke:#90CAF9,color:#333
    style Storm  fill:#4527A0,stroke:#311B92,color:#fff
```

---

## 🛠️ Tech Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| Language | ![Kotlin](https://img.shields.io/badge/-Kotlin-7F52FF?logo=kotlin&logoColor=white) | 100% Kotlin codebase |
| UI | ![Compose](https://img.shields.io/badge/-Jetpack%20Compose-4285F4?logo=jetpackcompose&logoColor=white) | Declarative modern UI |
| Architecture | MVVM + StateFlow | Reactive, lifecycle-aware state |
| Navigation | Compose Navigation | `@Serializable` type-safe routes |
| Networking | Retrofit2 + OkHttp | REST API + logging interceptor |
| Serialization | Kotlinx Serialization | Efficient JSON parsing |
| Local DB | Room | Favorite city persistence |
| Location | FusedLocationProvider | Precise + battery-efficient GPS |
| Geocoding | Android Geocoder | Coords → Human-readable city name |
| Weather API | [Open-Meteo](https://open-meteo.com/) | Free, no API key, highly accurate |
| CI/CD | GitHub Actions | Auto build on every push |

---

## ⚙️ CI/CD Pipeline

```mermaid
flowchart LR
    A["📝 git push\nto master"] --> B["🔄 GitHub Actions\nTriggered"]
    B --> C["☕ Setup\nJDK 17"]
    C --> D["🔧 Grant Gradle\nPermissions"]
    D --> E["🏗️ ./gradlew\nassembleDebug"]
    E --> F{{"Build\nResult"}}
    F -->|"✅ Success"| G["🟢 Badge: Passing"]
    F -->|"❌ Failure"| H["🔴 Badge: Failing"]

    style A fill:#24292e,color:#fff,stroke:#444
    style B fill:#0366d6,color:#fff,stroke:#0250ae
    style G fill:#2ea44f,color:#fff,stroke:#26933f
    style H fill:#d73a49,color:#fff,stroke:#c0303e
    style F fill:#735c0f,color:#fff,stroke:#5a4500
```

---

## 🚀 Quick Start

### Prerequisites
- Android Studio **Hedgehog** or newer
- Android SDK **26+**
- Device/emulator with Google Play Services

### Steps

```bash
# 1. Clone
git clone https://github.com/berat-karabuga/Mini-Weather-Fetcher.git
cd Mini-Weather-Fetcher

# 2. Open in Android Studio
# File → Open → Select folder → Sync Now

# 3. Build via terminal (optional)
./gradlew assembleDebug

# 4. Run on device
# ▶ in Android Studio or adb install
```

> ✅ **Zero configuration.** No `.env`, no API keys, no secrets. Just clone and run.

---

## 📱 Runtime Permissions

| Permission | Reason |
|-----------|--------|
| `ACCESS_FINE_LOCATION` | Precise GPS for automatic weather detection |
| `ACCESS_COARSE_LOCATION` | Fallback approximate location |
| `INTERNET` | Live weather + city search API calls |

---

## 🔮 Roadmap

- [ ] 🌡️ Fahrenheit / Celsius toggle
- [ ] 🌙 Dynamic dark mode
- [ ] 🔔 Background weather alerts
- [ ] 📍 Home screen widget
- [ ] 📊 Interactive temperature trend charts
- [ ] 🌍 EN / TR full language switch
- [ ] 🗺️ Map-based city picker
- [ ] 💾 Offline cached weather fallback

---

## 🤝 Contributing

```bash
git checkout -b feature/your-feature
git commit -m "feat: describe your change"
git push origin feature/your-feature
# → Open a Pull Request
```

Commit prefixes: `feat:` · `fix:` · `refactor:` · `docs:` · `chore:`

---

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:0288D1,100:4FC3F7&height=120&section=footer" width="100%"/>

**Made with ❤️ + ☕ + Kotlin**

[![GitHub](https://img.shields.io/badge/github-berat--karabuga-24292e?style=for-the-badge&logo=github)](https://github.com/berat-karabuga)

*If this project was useful to you, a ⭐ goes a long way!*

</div>
