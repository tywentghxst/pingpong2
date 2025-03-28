# Android Ping Pong Game

A simple flat ping pong game for Android devices. This game demonstrates the use of Android's SurfaceView and Canvas for game development.

## Game Features

- Simple paddle and ball mechanics
- Score tracking
- Game over screen with restart option
- Gradually increasing difficulty as ball speeds up

## How to Play

- Drag your finger horizontally to move the paddle
- Keep the ball from falling below the paddle
- Each time the ball hits the paddle, you score a point and the ball speeds up

## Project Structure

- `MainActivity.java` - Main activity that hosts the game view
- `PingPongView.java` - Custom SurfaceView that implements the game logic
- Resources for layouts, strings, and colors

## Building the Project

The project uses Gradle for building. You can build it in several ways:

### Using Android Studio

1. Open the project in Android Studio
2. Click 'Build' -> 'Build Bundle(s) / APK(s)' -> 'Build APK(s)'
3. The APK will be in `app/build/outputs/apk/debug/`

### Using Command Line

```bash
# On Linux/Mac
./gradlew assembleDebug

# On Windows
gradlew.bat assembleDebug
```

## GitHub Actions Workflow

This project includes a GitHub Actions workflow that automatically builds the app when changes are pushed to the main branch or pull requests are created. The workflow:

1. Sets up a JDK 11 environment
2. Builds the project with Gradle
3. Runs tests
4. Creates a debug APK
5. Uploads the APK as an artifact

## License

This project is available under the MIT License. See the LICENSE file for more information.
