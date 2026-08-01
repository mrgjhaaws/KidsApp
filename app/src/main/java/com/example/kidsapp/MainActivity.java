package com.example.kidsapp;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * MainActivity hosts the ShapesView and wires it up to:
 *  - Android's TextToSpeech engine, so tapped shapes are spoken aloud.
 *  - A Toast message, so the shape name is also shown as text on screen.
 *
 * Keeping Toast + TextToSpeech here (instead of inside ShapesView) keeps
 * the custom view reusable and focused purely on drawing/touch handling.
 */
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech textToSpeech;

    // Tracks whether the TextToSpeech engine finished initializing, so we
    // don't try to speak before it's ready.
    private boolean isTtsReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the text-to-speech engine. onInit() below is called
        // once it's ready to use.
        textToSpeech = new TextToSpeech(this, this);

        // Find our custom shapes view from the layout and listen for taps.
        ShapesView shapesView = findViewById(R.id.shapesView);
        shapesView.setOnShapeTouchedListener(this::onShapeTouched);
    }

    /**
     * Called by TextToSpeech once it has finished starting up.
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            isTtsReady = (result != TextToSpeech.LANG_MISSING_DATA
                    && result != TextToSpeech.LANG_NOT_SUPPORTED);
        }
    }

    /**
     * Called whenever a shape is tapped in the ShapesView.
     * Shows a Toast with the shape's name and speaks it aloud.
     */
    private void onShapeTouched(String shapeName) {
        Toast.makeText(this, shapeName, Toast.LENGTH_SHORT).show();
        speak(shapeName);
    }

    /**
     * Speaks the given text aloud, if the TTS engine is ready.
     */
    private void speak(String text) {
        if (isTtsReady) {
            // QUEUE_FLUSH interrupts any speech currently playing so taps
            // feel responsive even if the child taps shapes quickly.
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "shapeUtteranceId");
        }
    }

    /**
     * Always release TextToSpeech resources when the Activity is destroyed
     * to avoid memory leaks.
     */
    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
