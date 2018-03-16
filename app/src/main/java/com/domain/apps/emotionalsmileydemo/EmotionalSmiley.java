package com.domain.apps.emotionalsmileydemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import static android.graphics.Paint.Style.FILL;

/** Simple emotional smiley whose smile is controlled via MotionEvents.
 * Created by kedar on 3/13/18. Adapted from https://www.raywenderlich.com/175645/android-custom-view-tutorial
 */

public class EmotionalSmiley extends View {
    private static final String TAG = "EmotionalSmiley";
    private static final float DEFAULT_SMILE = 0.6f;
    private static final float maxSmile = 0.9f;
    private static final float minSmile = 0.3f;
    float borderWidth = 4.0f;
    float currentSmile = DEFAULT_SMILE;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int faceColor;//= Color.YELLOW;
    private int eyesColor;// = Color.BLACK;
    private int mouthColor;// = Color.BLACK;
    private int borderColor;// = Color.BLACK;
    private int size = 320;
    private Path mouthPath = new Path();

    public EmotionalSmiley(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EmotionalSmiley,0,0);
        try{
            currentSmile =  array.getFloat(R.styleable.EmotionalSmiley_startSmile,DEFAULT_SMILE);
            faceColor = array.getColor(R.styleable.EmotionalSmiley_faceColor,Color.YELLOW);
            eyesColor = array.getColor(R.styleable.EmotionalSmiley_eyesColor,Color.BLACK);
            mouthColor = array.getColor(R.styleable.EmotionalSmiley_mouthColor,Color.BLACK);
            borderColor = array.getColor(R.styleable.EmotionalSmiley_borderColor,Color.BLACK);

        } finally {
            array.recycle();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawFaceBackground(canvas);
        drawEyes(canvas);
        drawMouth(canvas);
    }

    //See https://stackoverflow.com/questions/12266899/onmeasure-custom-view-explanation
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        size = Math.min(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.getSize(widthMeasureSpec));
        setMeasuredDimension(size, size);
    }

    private void drawFaceBackground(Canvas canvas) {
        paint.setColor(faceColor);
        paint.setStyle(FILL);
        float radius = size / 2f;
        canvas.drawCircle(size / 2f, size / 2f, radius, paint);
        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);

        canvas.drawCircle(size / 2f, size / 2f, radius - borderWidth / 2f, paint);
    }

    private void drawEyes(Canvas canvas) {
        paint.setColor(eyesColor);
        paint.setStyle(FILL);
        RectF leftRect = new RectF(size * 0.32f, size * 0.23f, size * 0.4f, size * .39f);
        canvas.drawOval(leftRect, paint);
        RectF rightRect = new RectF(size * 0.6f, size * 0.23f, size * 0.68f, size * .39f);
        canvas.drawOval(rightRect, paint);
    }

    private void drawMouth(Canvas canvas) {
        mouthPath.reset();  //without a reset, you would end up with multiple "mouths" because the mouthPath object remembers its previous paths
        mouthPath.moveTo(size * 0.22f, size * 0.7f);
        mouthPath.quadTo(size * 0.5f, size * currentSmile, size * 0.78f, size * 0.7f);
        mouthPath.quadTo(size * 0.5f, size * (currentSmile + 0.15f), size * 0.22f, size * 0.7f);
        paint.setColor(mouthColor);
        paint.setStyle(FILL);
        canvas.drawPath(mouthPath, paint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //make sure these are in our bounds
        Rect myRect = new Rect();
        getDrawingRect(myRect);
        if (!myRect.contains((int) event.getX(), (int) event.getY())) {
            Log.v(TAG, "Out of my bounds");
            return true;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_UP:
                performClick();
                break;
            default:
                break;
        }
        //set our smile to current Y scaled between our smile range
        currentSmile = minSmile + event.getY() * (maxSmile - minSmile) / myRect.width();
        if (currentSmile > maxSmile) {
            currentSmile = maxSmile;
        }
        if (currentSmile < minSmile) {
            currentSmile = minSmile;
        }
        invalidate();
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
