package com.niterations.utils.gui.color;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.niterations.utils.BuildConfig;
import com.niterations.utils.R;

/**
 * Colorchooser dialog fragment
 * This uses the colors defined in values/colors.xml
 * @author n-iterations
 *
 */
public class ColorChooserFragment extends DialogFragment 
{
    private static final String LOG_TAG = ColorChooserFragment.class.getSimpleName();
    
    private static final String KEY_CALLER_TAG = ColorChooserFragment.class.getName() + ".KEY_CALLER_TAG";
    
    
    private LinearLayout mHorizontalLayout;
    
    private View mParentView;
    
    private ColorChooserCallbackIF mCallback;
    
    /**
     * We need the calling activity's package name because this is just a library 
     * package. when the resource R file is generated in the actual Android project,
     * the set of color values from values/colors.xml will be added to the R file of the
     * Android project. So we need to reference those colors using the actual package name
     * instead of the package name of this lib. 
     */
    private String mCallerPackageName;
    
    /**
     * This is the full class name of the calling fragment. 
     * See <a href="http://n-iterations.com/fragment-communication-pattern/">Fragment Communication Pattern</a>
     */
    private String mCallerTag;
    
    
    public static ColorChooserFragment newInstance(String callerTag)
    {
        ColorChooserFragment frag = new ColorChooserFragment();
        
        Bundle args = new Bundle();
        args.putString(KEY_CALLER_TAG, callerTag);
        frag.setArguments(args);
        
        return frag;
    }
    
    public ColorChooserFragment() { }
    
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        getDialog().setTitle(getResources().getString(R.string.DIALOG_COLOR_TITLE));
        mParentView = inflater.inflate(R.layout.color_chooser_dialog, container);
        
        mCallerTag = getArguments().getString(KEY_CALLER_TAG);
        
        mCallerPackageName = getActivity().getApplicationInfo().packageName;
        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Package name is "+ mCallerPackageName);
        
        initComponents(mParentView);
        
        mParentView.requestLayout();
        
        return mParentView;
    }

    private void initComponents(View view)
    {
        mHorizontalLayout = (LinearLayout)view.findViewById(R.id.colorChooserSampleLayout);
        
        Spinner colorTypeChooser = (Spinner) view.findViewById(R.id.colorTypeSpinner);
        colorTypeChooser.setOnItemSelectedListener(new OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                    int position, long id)
            {
                String[] colorTypes = getResources().getStringArray(R.array.COLOR_TYPE);
                
                if (position >= colorTypes.length)
                {
                    Log.e(LOG_TAG, "Selected color type position outide range " + position + " - "+ colorTypes.length);
                    return;
                }
                
                final String colorType = colorTypes[position].replace('/', '_');
                
                final int color_type_id = getResources().getIdentifier(colorType, "array", mCallerPackageName);
                
                if (color_type_id == 0)
                {
                    Log.e(LOG_TAG, "Unable to find color type for " + colorType + " in package " +mCallerPackageName);
                    String toast_message = String.format(getResources().getString(R.string.TOAST_NO_COLOR_TYPE), colorType);
                    Toast.makeText(getActivity(), toast_message, Toast.LENGTH_SHORT).show();
                    return;
                }
                
                updateColorPalette(color_type_id);
                
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                // do nothing
                
            }
        });
        
        int colorType = R.array.Whites_Pastel;
        updateColorPalette(colorType);
           
    }



    /**
     * @param horizLayout
     */
    private void updateColorPalette(int colorTypeResId)
    {
        String[] pastel_color_names = getResources().getStringArray(colorTypeResId);
        
        LayoutParams layoutParams = new LayoutParams();
        layoutParams.width = 64;
        layoutParams.height = 64;
        
        // remove the existing subset of color palette so that we can set the group selected.
        mHorizontalLayout.removeAllViews();
        
        for( final String name : pastel_color_names )
        {
            final int color_id = getResources().getIdentifier(name, "color", mCallerPackageName);
            
            if (color_id == 0)
            {
                Log.d(LOG_TAG, "Unable to find color resource " + name + " in package " + mCallerPackageName);
                break;
            }
            final int color = getResources().getColor(color_id);
            
            View v = new View(getActivity());
            v.setBackgroundColor(color);
            v.setLayoutParams(layoutParams);
            
            v.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View v)
                {
                    Log.d(LOG_TAG, "selected color " + name);
                    
                    mCallback.selectedColor(mCallerTag, color);
                    String toast_msg = String.format(getResources().getString(R.string.TOAST_SELECTED_COLOR), name);
                    Toast.makeText(getActivity(), toast_msg, Toast.LENGTH_SHORT).show();
                    
                    ColorChooserFragment.this.dismiss();
                    
                }
            });
            
            mHorizontalLayout.addView(v);
            
        }
    }

    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (ColorChooserCallbackIF) activity;
        } 
        catch( ClassCastException cce )
        {
            Log.e(LOG_TAG, "Activity " + activity.toString() + " needs to implement ColorChooserCallbackIF", cce);
            throw cce;
        }
    }
}
