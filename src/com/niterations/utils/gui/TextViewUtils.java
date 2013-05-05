package com.niterations.utils.gui;

import android.text.Editable;
import android.util.Log;
import android.widget.TextView;

public class TextViewUtils
{
    
    private static final String ZERO = "0";
    private static final String BLANK = "";
    
    public static int parseInt(TextView txt, int defaultValue)
    {
        if (txt == null) return defaultValue;
        if (txt.getText() == null || txt.getText().length() == 0) return defaultValue;
        
        try
        {
            return Integer.parseInt(txt.getText().toString());
        }
        catch( NumberFormatException nfe)
        {
            Log.e("TextViewUtils", "Unable to parse " + txt.getText().toString(), nfe);
            return defaultValue;
        }
    }
    
    
    public static int parseInt(Editable s, int defaultValue)
    {
        if (s == null || s.length() == 0) return defaultValue;
        
        try
        {
            return Integer.parseInt(s.toString());
        }
        catch( NumberFormatException nfe)
        {
            Log.e("TextViewUtils", "Unable to parse " + s.toString(), nfe);
            return defaultValue;
        }
    }
    
    public static String formatTime(int hour, int min, String template)
    {
        final String hourPrefix = (hour < 10) ? ZERO : BLANK;
        final String minPrefix = (min < 10) ? ZERO : BLANK;
        
        return String.format(template, hourPrefix + hour, minPrefix + min);
        
    }
    
    public static String formatTime(int hour, int min, int secs, int millis, String template)
    {
        final String hourPrefix = (hour < 10) ? ZERO : BLANK;
        final String minPrefix = (min < 10) ? ZERO : BLANK;
        final String secPrefix = (secs < 10) ? ZERO : BLANK;
        
        final int micro = millis / 10;
        final String microPrefix = (micro < 10) ? ZERO : BLANK;
        
        
        return String.format(template, hourPrefix + hour, minPrefix + min, secPrefix + secs, microPrefix + micro);
        
    }
    
   
}
