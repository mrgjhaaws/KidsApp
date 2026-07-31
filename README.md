# Kids Shapes App

A beginner-friendly Android app for kids, built with Java and XML.

## What it does

- Draws several colorful geometric shapes (circle, square, rectangle, triangle) in a grid.
- Highlights a shape with a new color when the child taps it.
- Plays a short "pop" bounce animation on the tapped shape.
- Shows a Toast message with the shape's name.
- Speaks the shape's name aloud using Android's built-in TextToSpeech engine.

## Project structure

```
KidsApp/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/kidsapp/
│       │   ├── MainActivity.java   # Hosts the view, wires up Toast + TextToSpeech
│       │   ├── ShapesView.java     # Custom View: drawing, touch detection, animation
│       │   └── Shape.java          # Simple data class describing one shape
│       └── res/
│           ├── layout/activity_main.xml
│           └── values/ (strings.xml, colors.xml, themes.xml)
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## How to open it

1. Open Android Studio.
2. Choose **Open**, and select this `KidsApp` folder.
3. Let Gradle sync, then press **Run** on an emulator or device (minSdk 21+).

## How each requirement is implemented

| Requirement | Where |
|---|---|
| Draw multiple colorful shapes | `ShapesView.onDraw()` + `drawShape()` |
| Highlight a shape when touched | `Shape.isHighlighted` + `ShapesView.onTouchEvent()` |
| Simple animation | `ShapesView.playPopAnimation()` (ValueAnimator "pop" scale) |
| Toast with shape name | `MainActivity.onShapeTouched()` |
| Speak the shape name | `MainActivity.speak()` using `android.speech.tts.TextToSpeech` |

## Extending it

- Add more shapes by adding entries in `ShapesView.buildShapes()`.
- Swap in different sounds/animations per shape type if you'd like more variety.
- Add a "Repeat" button that replays the last spoken shape name.
