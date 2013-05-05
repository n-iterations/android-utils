package com.niterations.utils.gui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;

public class GUIUtils
{

    public static Rect getTextBounds(Context ctx, Paint textPaint, String sampleText){
        Rect rect = new Rect();
        textPaint.getTextBounds(sampleText, 0,  sampleText.length(), rect);
        
        return rect;
    }
    
    public static int getActualTextSize(Context ctx, float preferredSize)
    {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (preferredSize * scale + 0.5f);
    }

}
