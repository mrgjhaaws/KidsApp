package com.example.kidsapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * ShapesView is a custom View that:
 *  1. Draws several colorful geometric shapes.
 *  2. Detects when a shape is touched.
 *  3. Highlights the touched shape (color change).
 *  4. Plays a simple "pop" scale animation on the touched shape.
 *  5. Reports the touched shape's name back to the Activity through a listener,
 *     so the Activity can show a Toast and speak the name out loud.
 */
public class ShapesView extends View {

    /**
     * Callback interface used to tell the Activity that a shape was tapped.
     * Keeping this logic in the Activity (Toast + TextToSpeech) keeps the
     * custom view focused only on drawing and touch handling.
     */
    public interface OnShapeTouchedListener {
        void onShapeTouched(String shapeName);
    }

    private OnShapeTouchedListener listener;

    // Paint object reused for all drawing (better performance than creating
    // a new Paint every time onDraw() runs).
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    // The list of shapes we draw on screen.
    private final List<Shape> shapes = new ArrayList<>();

    public ShapesView(Context context) {
        super(context);
        init();
    }

    public ShapesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Sets up the paint style. Shape positions are created later, once we
     * know the actual size of the view (see onSizeChanged).
     */
    private void init() {
        paint.setStyle(Paint.Style.FILL);
    }

    public void setOnShapeTouchedListener(OnShapeTouchedListener listener) {
        this.listener = listener;
    }

    /**
     * Called automatically by Android once the view's width and height are known.
     * We build our list of shapes here, arranged in a simple grid, so that they
     * always fit nicely on screen no matter the device size.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        buildShapes(w, h);
    }

    /**
     * Creates five colorful shapes arranged in a simple 2-row grid.
     */
    private void buildShapes(int width, int height) {
        shapes.clear();

        float margin = 40f;
        float cellWidth = (width - margin * 3) / 2f;
        float cellHeight = (height - margin * 3) / 2f;

        // Row 1
        RectF circleBounds = new RectF(margin, margin, margin + cellWidth, margin + cellHeight);
        RectF squareBounds = new RectF(margin * 2 + cellWidth, margin,
                margin * 2 + cellWidth * 2, margin + cellHeight);

        // Row 2
        RectF triangleBounds = new RectF(margin, margin * 2 + cellHeight,
                margin + cellWidth, margin * 2 + cellHeight * 2);
        RectF rectangleBounds = new RectF(margin * 2 + cellWidth, margin * 2 + cellHeight,
                margin * 2 + cellWidth * 2, margin * 2 + cellHeight * 2);

        // A smaller oval placed centered below, only added if there's room.
        shapes.add(new Shape(Shape.Type.CIRCLE, "Circle", circleBounds,
                Color.parseColor("#FF6B6B"), Color.parseColor("#FFD93D")));
        shapes.add(new Shape(Shape.Type.SQUARE, "Square", squareBounds,
                Color.parseColor("#4D96FF"), Color.parseColor("#FFD93D")));
        shapes.add(new Shape(Shape.Type.TRIANGLE, "Triangle", triangleBounds,
                Color.parseColor("#6BCB77"), Color.parseColor("#FFD93D")));
        shapes.add(new Shape(Shape.Type.RECTANGLE, "Rectangle", rectangleBounds,
                Color.parseColor("#B983FF"), Color.parseColor("#FFD93D")));

        invalidate(); // Ask Android to redraw now that shapes exist.
    }

    /**
     * Draws every shape in our list. Called automatically by Android
     * whenever the view needs to be redrawn (e.g. after invalidate()).
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Shape shape : shapes) {
            // Pick the color: highlight color if currently tapped, else normal color.
            paint.setColor(shape.isHighlighted ? shape.highlightColor : shape.normalColor);

            // Save the canvas state, apply the "pop" scale animation around
            // the shape's own center, draw the shape, then restore the canvas
            // so the scaling doesn't affect other shapes.
            canvas.save();
            canvas.scale(shape.animationScale, shape.animationScale, shape.centerX(), shape.centerY());
            drawShape(canvas, shape);
            canvas.restore();
        }
    }

    /**
     * Draws a single shape according to its type.
     */
    private void drawShape(Canvas canvas, Shape shape) {
        RectF b = shape.bounds;

        switch (shape.type) {
            case CIRCLE:
                float radius = Math.min(b.width(), b.height()) / 2f;
                canvas.drawCircle(b.centerX(), b.centerY(), radius, paint);
                break;

            case SQUARE:
                // Draw a square centered inside the bounding box.
                float side = Math.min(b.width(), b.height());
                RectF squareRect = new RectF(
                        b.centerX() - side / 2f, b.centerY() - side / 2f,
                        b.centerX() + side / 2f, b.centerY() + side / 2f);
                canvas.drawRect(squareRect, paint);
                break;

            case RECTANGLE:
                canvas.drawRect(b, paint);
                break;

            case OVAL:
                canvas.drawOval(b, paint);
                break;

            case TRIANGLE:
                Path trianglePath = new Path();
                trianglePath.moveTo(b.centerX(), b.top);       // top point
                trianglePath.lineTo(b.left, b.bottom);          // bottom left
                trianglePath.lineTo(b.right, b.bottom);         // bottom right
                trianglePath.close();
                canvas.drawPath(trianglePath, paint);
                break;
        }
    }

    /**
     * Handles touch events. When the user taps down on a shape, we:
     *  1. Mark it as highlighted.
     *  2. Play a short "pop" animation.
     *  3. Notify the Activity so it can show a Toast and speak the name.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            // Check shapes from the last drawn (top) to the first, in case
            // shapes ever overlap in a future layout.
            for (int i = shapes.size() - 1; i >= 0; i--) {
                Shape shape = shapes.get(i);
                if (shape.contains(touchX, touchY)) {
                    handleShapeTapped(shape);
                    return true; // We handled this touch.
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * Turns on the highlight color, plays the pop animation, and reports
     * the tap to the Activity via the listener.
     */
    private void handleShapeTapped(Shape shape) {
        shape.isHighlighted = true;
        invalidate();
        playPopAnimation(shape);

        if (listener != null) {
            listener.onShapeTouched(shape.name);
        }
    }

    /**
     * A simple "pop" animation: scales the shape up and back down using a
     * ValueAnimator. OvershootInterpolator gives it a playful bounce, which
     * kids tend to enjoy.
     */
    private void playPopAnimation(Shape shape) {
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 1.3f, 1f);
        animator.setDuration(400);
        animator.setInterpolator(new OvershootInterpolator());

        // Every time the animated value updates, store it on the shape and
        // redraw the view so the change is visible.
        animator.addUpdateListener(animation -> {
            shape.animationScale = (float) animation.getAnimatedValue();
            invalidate();
        });

        // Once the animation finishes, turn off the highlight color so the
        // shape returns to its normal look.
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                shape.isHighlighted = false;
                invalidate();
            }
        });

        animator.start();
    }
}
