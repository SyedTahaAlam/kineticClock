# kineticClock

A Kotlin + Compose Multiplatform kinetic wall clock inspired by kinetic clock art pieces where tiny clock hands rotate together to form the current time.

## Features

- Smooth real-time kinetic animation across a matrix of tiny clock hands
- Clean immersive full-screen display with no controls on screen
- Android phone + Android TV support (landscape-first, keep screen on, immersive mode)
- TV remote / D-pad friendly input (toggle 12/24h and tune animation speed)
- Desktop support for Windows, macOS, Linux with full-screen by default and wake-lock helper
- Desktop packaging for `dmg` (macOS), `msi` (Windows), and `deb` (Linux)

## Run

### Android

```bash
./gradlew :composeApp:installDebug
```

### Desktop

```bash
./gradlew :composeApp:run
```

## Package desktop apps

```bash
./gradlew :composeApp:packageDistributionForCurrentOS
```

## Controls

- `Enter` / `D-pad center` / `Space`: toggle 12h ↔ 24h
- `D-pad` arrows: adjust animation softness/speed
- Desktop: `F` toggles fullscreen, `Esc` exits fullscreen (or closes when windowed)
