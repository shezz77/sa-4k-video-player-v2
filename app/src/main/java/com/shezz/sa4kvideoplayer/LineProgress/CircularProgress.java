

package com.shezz.sa4kvideoplayer.LineProgress;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;


public class CircularProgress extends BaseProgressView {

    private static final int ANGLE_360 = 360;
    private RectF rectF;
    private float bottom;
    private float right;
    private float top;
    private float left;
    private float angle;

    private int min;
    private float angleX;
    private float angleY;
    private final float ANGLE_180 = 180f;
    private final int angTxtMargin = 5;
    private final int startAngle0 = -80;
    private final int startAngle1 = -92;
    private float startX, startY;
    private int circleTxtPadding;
    private int txtMarginX;
    private int txtMarginY;

    public CircularProgress(Context context) {
        super(context);
    }

    public CircularProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init(Context ctx) {
        context = ctx;
        rectF = new RectF();
        setProgress(0);
        initBackgroundColor();
        initForegroundColor();
        initTextColor();
    }

    @Override
    protected void initBackgroundColor() {
        super.initBackgroundColor();
    }

    @Override
    protected void initForegroundColor() {
        super.initForegroundColor();
    }

    @Override
    protected void initTextColor() {
        super.initTextColor();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        angle = ANGLE_360 * progress / maximum_progress;
        canvas.drawArc(rectF, startAngle0, angle - ANGLE_360, false, backgroundPaint);
        if (progress > 1){
            canvas.drawArc(rectF, startAngle1, angle, false, foregroundPaint);
        }
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        angleX = (float) ((angle + 1.5) * Math.PI / ANGLE_180);
        angleY = (float) ((angle + 2) * Math.PI / ANGLE_180);

        startX = (float) (min / 2 - angTxtMargin + rectF.width() / 2 * Math.sin(angleX));
        startY = (float) (min / 2 + angTxtMargin - rectF.width() / 2 * Math.cos(angleY));

        if (progress > 98) {
            canvas.save();

            txtMarginX = 30;
            txtMarginY = -10;
            canvas.rotate(angle, startX, startY);

            canvas.drawText(String.format("%d%%", progress), startX - txtMarginX, startY + txtMarginY,
                    textPaint);
            canvas.restore();
        } else {
            txtMarginX = 18;
            txtMarginY = 25;
            canvas.drawText(String.format("%d%%", progress), startX - txtMarginX, startY + angTxtMargin,
                    textPaint);
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        min = setDimensions(widthMeasureSpec, heightMeasureSpec);
        circleTxtPadding = 30;

        left = 0 + strokeWidth / 2;
        top = 0 + strokeWidth / 2;
        right = min - strokeWidth / 2;
        bottom = min - strokeWidth / 2;
        rectF.set(left + PADDING + circleTxtPadding, top + PADDING + circleTxtPadding, right - PADDING - circleTxtPadding, bottom
                - PADDING - circleTxtPadding);
    }

    protected int setDimensions(int widthMeasureSpec, int heightMeasureSpec) {
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        final int smallerDimens = Math.min(width, height);

        setMeasuredDimension(smallerDimens, smallerDimens);
        return smallerDimens;
    }
}
