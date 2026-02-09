[中文版](./README_zh.md) | [English](./README.md)

# Sokoban Game

A classic Sokoban puzzle game implemented in Java Swing with custom audio feedback system.

## Project Overview

Sokoban is a traditional puzzle game where players push boxes to designated target positions. This project implements the complete game logic, graphical interface, and audio feedback using the Java Swing framework.

## Key Features

### Core Gameplay
- **Multi-Level Design**: 4 carefully designed levels with progressive difficulty
- **Step Counter**: Real-time display of current level move count
- **Level Reset**: Press `R` to quickly reset the current level
- **Level Progression**: Automatic advancement to the next level upon completion

### Controls

| Key | Function |
|-----|----------|
| `↑` `↓` `←` `→` | Move player |
| `R` | Reset current level |
| `M` | Toggle mute/unmute |

### Technical Highlights
- **Custom Audio System**: Real-time audio generation using `javax.sound.sampled`
  - Movement sound effects
  - Box pushing sounds
  - Wall collision sounds
  - Victory sounds
  - Background music (BGM)
- **Asynchronous Audio Playback**: Sound effects play in separate threads, non-blocking to UI
- **Double-Buffered Rendering**: Utilizes Swing's default double-buffering mechanism for smooth animations
- **Dual-Mode Image Loading**: Supports both class path and file system resource loading
- **Fallback Rendering**: Automatic graphical fallback when image loading fails

## Technology Stack

| Component | Technology |
|-----------|------------|
| Language | Java |
| GUI Framework | Java Swing (JFrame, JPanel) |
| Event Handling | AWT KeyAdapter |
| Audio Processing | Java Sound API (`javax.sound.sampled`) |
| Rendering | Swing double-buffering |

## Project Structure

```
SokobanGame/
├── MovingBox.java      # Main application entry
├── kirby.jpg           # Player character sprite
├── download.png        # Box sprite
└── README_zh.md        # Chinese documentation
```

## Installation & Usage

### Prerequisites
- JDK 8 or higher

### Compile & Run
```bash
# Compile
javac MovingBox.java

# Run
java PushingBoxGame.MovingBox
```

Alternatively, run the main class `MovingBox` directly from an IDE (IntelliJ IDEA, Eclipse).

## Game Elements

| Symbol | Description |
|--------|-------------|
| `W` | Wall (impassable) |
| `T` | Target point |
| `B` | Box |
| `P` | Player |
| `.` | Floor |
| `*` | Completed box (pushed to target) |

## Development Notes

### Level Design
Level data is stored in the `levels` two-dimensional array. Each level is a string array. New levels can be added in the following format:

```java
{"WWWWWW",
 "W.T..W",
 "W.B.TW",
 "W.P..W",
 "WWWWWW"}
```

### Audio Customization
Modify the `playTone(int freq, int ms)` method to adjust sound effect parameters:
- `freq`: Audio frequency (Hz)
- `ms`: Duration (milliseconds)

## License

MIT License - see [LICENSE](LICENSE) for details.
