package com.elmpool.tallr.widgets;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.preference.ListPreference;

import com.elmpool.tallr.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Arrays;

public class MaterialListPreference extends ListPreference {

    public MaterialListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public MaterialListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MaterialListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaterialListPreference(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
        int index = Math.max(Arrays.asList(getEntryValues()).indexOf(getValue()), 0);
        new MaterialAlertDialogBuilder(getContext(), R.style.MaterialAlertDialog_Rounded)
                .setSingleChoiceItems(getEntries(), index, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        dialog.dismiss();
                        if(callChangeListener(getEntryValues()[index].toString())){
                            setValueIndex(index);
                        }
                    }
                })
                .setNegativeButton(getNegativeButtonText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .setTitle(getDialogTitle())
                .setIcon(getDialogIcon())
                .show();
    }

}
