package com.niterations.utils.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class FlexibleScrollView extends ScrollView
{
    
    private volatile boolean mDisableScrolling = false;

    public FlexibleScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public FlexibleScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

    }

    public FlexibleScrollView(Context context)
    {
        super(context);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:

            if (!mDisableScrolling) return super.onTouchEvent(ev);
            return !mDisableScrolling; // mScrollable is always false at this point
        default:
            return super.onTouchEvent(ev);
        }
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Don't do anything with intercepted touch events if 
        // we are not scrollable
        if (mDisableScrolling) return false;
        else return super.onInterceptTouchEvent(ev);
    }
    
    public void disableScrolling()
    {
        mDisableScrolling = true;
    }
    
    public void enableScrolling()
    {
        mDisableScrolling = false;
    }

    
    

}
