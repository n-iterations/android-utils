package com.niterations.utils.gui.color;

/**
 * Callback for the ColorChooserFragment dialog
 * @author n-iterations
 *
 */
public interface ColorChooserCallbackIF
{
    /**
     * Callback method to the caller to notify the color that the user has selected.
     * @param callerTag use to identify the actual fragment that invokes the ColorChooserFragment dialog.
     * This uses the <a href="http://n-iterations.com/fragment-communication-pattern/">Fragment Communication Pattern</a>
     * to pass the color to the correct calling fragment.
     * @param color the color using generated values from values/colors.xml. Note: this is <em>not</em>
     * the RGB color values from java.awt.Color 
     */
    void selectedColor(String callerTag, int color);
}
