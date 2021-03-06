package com.flowercentral.flowercentralbusiness.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Custom circular text view.
 */
public class CircularTextView extends AppCompatTextView {
    private float strokeWidth;
    int strokeColor, solidColor;

    /**
     * Constructor init the context to AppCompatTextView
     *
     * @param context context
     */
    public CircularTextView(Context context) {
        super(context);
    }

    /**
     * Constructor init the context, attribute set to AppCompatTextView
     *
     * @param context context
     * @param attrs   attrs
     */
    public CircularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Constructor init the context, attribute set and style attr to AppCompatTextView
     *
     * @param context context
     * @param attrs   attrs
     */
    public CircularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    /**
     * Draw circular view via paint.
     *
     * @param canvas canvas
     */
    @Override
    public void draw(Canvas canvas) {

        Paint circlePaint = new Paint();
        circlePaint.setColor(solidColor);
        circlePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        Paint strokePaint = new Paint();
        strokePaint.setColor(strokeColor);
        strokePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        int h = this.getHeight();
        int w = this.getWidth();

        int diameter = ((h > w) ? h : w);
        int radius = diameter / 2;

        this.setHeight(diameter);
        this.setWidth(diameter);

        canvas.drawCircle(diameter / 2, diameter / 2, radius, strokePaint);

        canvas.drawCircle(diameter / 2, diameter / 2, radius - strokeWidth, circlePaint);

        super.draw(canvas);
    }

    /**
     * Set the stroke width.
     *
     * @param dp width in dp
     */
    public void setStrokeWidth(int dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        strokeWidth = dp * scale;
    }

    /**
     * Sets the stroke color.
     *
     * @param color color
     */
    public void setStrokeColor(int color) {
        strokeColor = color;
    }

    /**
     * Circle solid color.
     *
     * @param color color
     */
    public void setSolidColor(int color) {
        solidColor = color;
    }
}
