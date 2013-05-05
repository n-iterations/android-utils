package com.niterations.utils.gui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

import com.niterations.utils.BuildConfig;
import com.niterations.utils.R;

public class TimeClockViewRenderer
{
    
    private static final String LOG_TAG = TimeClockViewRenderer.class.getSimpleName();
    
    private static final String SAMPLE_TIME_STRING = "00:00";
    
    private static final int MAIN_CIRCLE_PADDING = 10;
    
    private volatile int mHour, mMinute;
    
    private View mParent;
    
    private Paint mPaintLine, mPaintText, mPaintIndicator;
    
    private boolean mInitialized = false;
    private int mCanvasWidth, mCanvasHeight, mHeightCenter, mWidthCenter, mRadius;
    
    private float mTextStringWidth, mTextStringHeight;
    
    private double mSine, mCosine;
    
    
    
    public TimeClockViewRenderer(View parent)
    { 
        mParent = parent;
        init();
    }

    public TimeClockViewRenderer(View parent, int hour, int min) 
    {
        mParent = parent;
        mHour = hour;
        mMinute = min;
        
        init();
    }
    
    private void init()
    {
        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintLine.setColor(mParent.getContext().getResources().getColor(R.color.Black));
        mPaintLine.setStyle(Style.STROKE);
        mPaintLine.setStrokeWidth(2);
        
        mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintIndicator.setColor(mParent.getContext().getResources().getColor(R.color.OrangeRed));
        mPaintIndicator.setStyle(Style.FILL);
        mPaintIndicator.setStrokeWidth(2);
        
        
        mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintText.setColor(mParent.getResources().getColor(R.color.Black));
        mPaintText.setStyle(Style.FILL);
        mPaintText.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.BOLD));
        
        final int actualTextSize = GUIUtils.getActualTextSize(mParent.getContext(), 20.0f);
        mPaintText.setTextSize(actualTextSize);
        
        Rect textBounds = GUIUtils.getTextBounds(mParent.getContext(), mPaintText, SAMPLE_TIME_STRING);
        mTextStringWidth = textBounds.width();
        mTextStringHeight = textBounds.height();
    }
    
    
    public void refresh(int hour, int min)
    {
        mHour = hour;
        mMinute = min;
        
        final int hourInTwelve = mHour % 12;
        final double degrees = 30d * hourInTwelve + Math.floor(mMinute / 2 );
        final double radians = degrees * (Math.PI / 180d);
        
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "(degrees/radians) " + degrees + "," + radians);
        mSine = Math.sin(radians);
        mCosine =  Math.cos(radians);
        
        
    }
    
    protected void render(Canvas canvas)
    {
        if (!mInitialized)
        {
            mCanvasWidth = mParent.getWidth();
            mCanvasHeight = mParent.getHeight();
            
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Canvas width and height " + mCanvasWidth + ", " + mCanvasHeight); 
            
            mHeightCenter = mCanvasHeight / 2;
            mWidthCenter = mCanvasWidth / 2;
            
            mRadius = (mCanvasHeight < mCanvasWidth) ? mHeightCenter - MAIN_CIRCLE_PADDING : mWidthCenter - MAIN_CIRCLE_PADDING;
            
            mInitialized = true;
        }
        
        if (mHour >= 6 && mHour < 18)
            mPaintLine.setColor(mParent.getResources().getColor(R.color.Orange));
        else
            mPaintLine.setColor(mParent.getResources().getColor(R.color.Black));
            
        
        canvas.drawCircle(mWidthCenter, mHeightCenter, mRadius, mPaintLine);
        final String timeString = TextViewUtils.formatTime(mHour, mMinute, mParent.getResources().getString(R.string.TIME_TEMPLATE)); 
        canvas.drawText(timeString, mWidthCenter - (mTextStringWidth / 2), mHeightCenter + (mTextStringHeight / 2), mPaintText);
        
        
        final float indicatorX = (float) (mWidthCenter + mRadius * mSine);
        final float indicatorY = (float) (mHeightCenter - mRadius * mCosine);
        
        canvas.drawCircle(indicatorX, indicatorY, 8, mPaintIndicator);
        
        
    }

}
