package com.niterations.utils.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.niterations.utils.BuildConfig;


public class TimeClockView extends View
{
    private static final String LOG_TAG = TimeClockView.class.getSimpleName();
    
    private static final int TOLERANCE = 1;
    
    private static final int MINUTE_INCREMENTAL = 5;
    
    private volatile int mHour, mMinute;
    
    private TimeClockViewRenderer mRenderer;
    
    private volatile boolean mTouchMove;
    
    private float  mInitialY;
    
    private boolean mIsPastNoon;
    
    private FlexibleScrollView mFlexiScroll;
    
    private OnTimeChangedListener mTimeChangedListener = new OnTimeChangedListener()
    {
        
        @Override
        public void onTimeChanged(TimeClockView view, int hourOfDay, int minute)
        {
            // do nothing, default behaviour to be overridden
        }
    };
    
    
    public interface OnTimeChangedListener
    {
        void onTimeChanged(TimeClockView view, int hourOfDay, int minute);
    }

    public TimeClockView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mRenderer = new TimeClockViewRenderer(this);
    }

    public TimeClockView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mRenderer = new TimeClockViewRenderer(this);
    }

    public TimeClockView(Context context)
    {
        super(context);
        mRenderer = new TimeClockViewRenderer(this);
    }
    
    
    public void setScrollView(FlexibleScrollView flexiScrollView)
    {
        mFlexiScroll = flexiScrollView;
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        if (mRenderer == null) return;
        mRenderer.render(canvas);
    }
    
    public void setOnTimeChangedListener(OnTimeChangedListener timeChangedListener)
    {
        mTimeChangedListener = timeChangedListener;
    }
    
    public int getCurrentHour()
    {
        return mHour;
    }
    
    public int getCurrentMinute()
    {
        return mMinute;
    }
    
    public void setCurrentHour(int hour)
    {
        mHour = hour;
        if (mHour >= 12 ) mIsPastNoon = true;
        mRenderer.refresh(mHour, mMinute);
        mTimeChangedListener.onTimeChanged(this, mHour, mMinute);
        invalidate();
    }
    
    public void setCurrentMinute(int min)
    {
        mMinute = min;
        mRenderer.refresh(mHour, mMinute);
        mTimeChangedListener.onTimeChanged(this, mHour, mMinute);
        invalidate();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onTouchDown");
            
            mInitialY = event.getY();
            
            if (mFlexiScroll != null) mFlexiScroll.disableScrolling();
            
            return true;
        }
        
        
        
        if ((event.getAction() & MotionEvent.ACTION_MOVE) == MotionEvent.ACTION_MOVE )
        {            
            mTouchMove = true;
            
            final float widthCenter = getWidth() / 2;
            final float heightCenter = getHeight()  /2;
            
            final double o = event.getX() - widthCenter;
            final double a = heightCenter - event.getY();
            
            final double radians = Math.atan(o/a);
            double degrees = radians * 180 / Math.PI;
            
            // second, third quadrant 90 to 270 degrees, 2nd quard degree is negative
            if ( a < 0)
                degrees = 180 + degrees;
            
            
            
            // fourth quadrant 270 to 360 degrees, degress is negative
            if (o < 0 && a > 0)
                degrees = 360 + degrees;
            
            
            int hour = (int) Math.floor(12 * degrees / 360);
            int minute =  (int) Math.floor(( 60 * (12 * degrees / 360  -  hour)));
            
            if ((minute % MINUTE_INCREMENTAL) < ((double)MINUTE_INCREMENTAL / 2.0))
                minute = minute - (minute % MINUTE_INCREMENTAL);
            else
                minute = (minute + (MINUTE_INCREMENTAL - (minute % MINUTE_INCREMENTAL)));
            
            if (minute >= 60) 
            {
                hour = (hour + 1) % 12;
                minute -= 60;
            }
            
            if ((mHour == 11 && hour == 0) ||
                 (mHour == 0 && hour == 11))
                mIsPastNoon = true;
                
            
            
            if ((mHour == 23 && hour == 0) ||
                 (mHour == 12 && hour == 11))
            {
                mIsPastNoon = false;
            }
            
            if( mIsPastNoon ) hour += 12;
            
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, "touch degree " + degrees + "( " + hour + "," + minute + ")");
            
            // tolerance. ignore fat finger.
            if ((mHour == 23 && hour == 0) || (mHour == 0 && hour == 23 ) ||  Math.abs(hour - mHour) <= 1)
            {
                mHour = hour;
                mMinute = minute;
                
                mRenderer.refresh(mHour, mMinute);
                invalidate();
                
                mTimeChangedListener.onTimeChanged(this, mHour, mMinute);
            }
  
            return true;
        }
        
        else if ((event.getAction() & MotionEvent.ACTION_UP) == MotionEvent.ACTION_UP)
        {
            if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onTouchUp");
            
            if (mFlexiScroll != null) mFlexiScroll.enableScrolling();
            
            if (mTouchMove || Math.abs(mInitialY - event.getY()) > TOLERANCE) {    
                mTouchMove = false;
                return false; // moving, so not tapping
            }
            
            
            
            final float radius = getHeight() / 2 - 5;
            final float minX = getWidth() / 2 - radius;
            final float maxX = getWidth() / 2 + radius;
            final float minY = getHeight() / 2 - radius;
            final float maxY = getHeight() / 2 + radius;
            
            final float x = event.getX();
            final float y = event.getY();
            
            if (x < minX || x > maxX || y < minY || y > maxY)
            {
                mTouchMove = false;
                return false;
            }
            
            if (event.getX() < getWidth() / 2) // hour
            {
                if(event.getY() < getHeight() / 2) // at the top
                    mHour = (mHour + 1) % 24;
                else mHour = (mHour == 0) ? 23 : (mHour - 1);
            }
            else // minute
            {
                if(event.getY() < getHeight() / 2) // at the top
                    mMinute = (mMinute + 1) % 60;
                else mMinute = (mMinute == 0) ? 59 : (mMinute - 1);
            }
            
            mRenderer.refresh(mHour, mMinute);
            mTimeChangedListener.onTimeChanged(this, mHour, mMinute);
            
            invalidate();
            
            return true;
        }
        
        return super.onTouchEvent(event);
    }
    
}
