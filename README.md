# DSN Explorer

A simple Android app for browsing NASA's Deep Space Network (DSN) live telemetry. Displays current antenna status, signals, and spacecraft targets across the three DSN complexes (Madrid, Goldstone, Canberra).

## Features

- Live DSN feed from `eyes.nasa.gov/dsn` — stations, dishes, signals (up/down), and targets
- Static configuration from `config.xml` — site/dish/spacecraft metadata
- Real-time signal monitoring with frequency, data rate, power, and direction
- Spacecraft range and light-time estimates, with JPL Horizons fallback for missing data
- Dish azimuth/elevation artwork from NASA's Eyes on the Solar System
- Spacecraft thumbnails and complex flags

## Architecture

- **Language:** Kotlin
- **UI:** Jetpack Compose (Material 3)
- **DI:** Koin
- **Networking:** Ktor Client (OkHttp)
- **XML parsing:** `XmlPullParser` (via `kxml2` on JVM, Android SDK on device)
- **Images:** Coil
- **Logging:** Timber

## Project structure

```
app/src/main/java/com/holdenisacat/dsnexplorer/
├── model/            # Immutable data models (Dsn, Site, Dish, Spacecraft, Signal, ...)
├── parser/           # XML feed parsers (DataXmlParser, ConfigXmlParser)
├── repository/       # RangeSupplementer, HorizonsRangeClient
├── util/             # Formatters, DsnApi, DsnXml, SiteFlags
└── ui/               # Jetpack Compose screens and components
```

## Build

Requires [Android Studio](https://developer.android.com/studio) or the Android SDK with `ANDROID_HOME` set.

```bash
# Compile debug Kotlin
./gradlew :app:compileDebugKotlin

# Run lint
./gradlew :app:lintDebug

# Run detekt static analysis
./gradlew :app:detekt

# Run unit tests
./gradlew :app:testDebugUnitTest
```

## Testing

Unit tests cover `Formatters`, `DataXmlParser`, `ConfigXmlParser`, and `RangeSupplementer`. Test XML fixtures live in `app/src/test/resources/`.

## CI

GitHub Actions runs compile, lint, detekt, and unit tests on every push and pull request (`.github/workflows/ci.yml`).

## License

MIT