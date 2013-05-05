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
        // TODO Auto-generated constructor stub
    }

    public FlexibleScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public FlexibleScrollView(Context context)
    {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
//        if (!mDisableScrolling)
//            return super.onTouchEvent(ev);
//        
//        return false;  // do nothing
        
        
        switch (ev.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // if we can scroll pass the event to the superclass
            if (!mDisableScrolling) return super.onTouchEvent(ev);
            // only continue to handle the touch event if scrolling enabled
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
