package fr.isen.shazamphoto.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ViewPagerOverlayFrame extends View {

    private Paint mPaint;

    public ViewPagerOverlayFrame(Context context) {
        super(context);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(60.0f);
    }

    public ViewPagerOverlayFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(60.0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, 0, getMeasuredWidth(), 0, mPaint);
        /*canvas.drawLine(getMeasuredWidth(), 0, getMeasuredWidth(),
                getMeasuredHeight(), mPaint);
        canvas.drawLine(getMeasuredWidth(), getMeasuredHeight(), 0,
                getMeasuredHeight(), mPaint);
        canvas.drawLine(0, getMeasuredHeight(), 0, 0, mPaint);*/
        Paint paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(20);
        String noInternet = "Internet is not available";
        canvas.drawText(noInternet, getMeasuredWidth()/2 - paintText.measureText(noInternet)/2, 20, paintText);
    }

}

