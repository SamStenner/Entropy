package com.elmpool.tallr.services;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.DimenRes;

public class Manager {

    public static void setStatusBar(Activity activity){
        Window window = activity.getWindow();
        int flags = window.getDecorView().getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        window.getDecorView().setSystemUiVisibility(flags);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    public static int convertDimension(Context context, @DimenRes int resource){
        Resources r = context.getResources();
        return (int) r.getDimension(resource);
    }

}
