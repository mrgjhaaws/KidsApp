package com.example.kidsapp;

import android.graphics.RectF;

/**
 * Shape is a simple data class that describes one shape drawn on screen.
 *
 * It stores:
 *  - what kind of shape it is (circle, square, etc.)
 *  - the name we show/speak for it (e.g. "Circle")
 *  - the area it occupies on screen (bounds)
 *  - its normal color and its "highlight" color (used when tapped)
 *  - a scale value used to animate a little "pop" when tapped
 */
public class Shape {

    // The different kinds of shapes our app can draw.
    public enum Type {
        CIRCLE,
        SQUARE,
        RECTANGLE,
        TRIANGLE,
        OVAL
    }

    public final Type type;
    public final String name;
    public final RectF bounds;      // The bounding box of the shape on screen
    public final int normalColor;   // Color shown normally
    public final int highlightColor; // Color shown briefly when tapped

    // animationScale controls how big the shape is drawn right now.
    // 1.0f = normal size. We animate this up and back down to make a "pop" effect.
    public float animationScale = 1.0f;

    // isHighlighted is true for a short moment right after the shape is tapped.
    public boolean isHighlighted = false;

    public Shape(Type type, String name, RectF bounds, int normalColor, int highlightColor) {
        this.type = type;
        this.name = name;
        this.bounds = bounds;
        this.normalColor = normalColor;
        this.highlightColor = highlightColor;
    }

    /**
     * Returns the center X coordinate of the shape's bounding box.
     * Used as the pivot point when animating (scaling) the shape.
     */
    public float centerX() {
        return bounds.centerX();
    }

    /**
     * Returns the center Y coordinate of the shape's bounding box.
     */
    public float centerY() {
        return bounds.centerY();
    }

    /**
     * Simple hit test: checks whether a touch point (x, y) falls inside
     * this shape's bounding box. This is beginner-friendly and works well
     * enough for square/rectangle/circle/oval shapes. For the triangle we
     * still use the bounding box for simplicity, which is fine for a kids app
     * since the triangle is drawn to roughly fill its box.
     */
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }
}
