package com.elmpool.tallr.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.DimenRes;
import androidx.annotation.StringRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.elmpool.tallr.BuildConfig;
import com.elmpool.tallr.R;

public class Manager {

    public static void setStatusBar(Activity activity){
        Window window = activity.getWindow();
        int flags = window.getDecorView().getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        window.getDecorView().setSystemUiVisibility(flags);
    }

    public static void setNavBar(final View rootView, final LinearLayout layout, final View scrollingView){
        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                layout.setPadding(0, 0, 0, insets.getSystemWindowInsetBottom());
                scrollingView.setPadding(
                        scrollingView.getPaddingLeft(),
                        scrollingView.getPaddingTop(),
                        scrollingView.getPaddingRight(),
                        convertDimension(layout.getContext(), R.dimen.send_padding)
                                + insets.getSystemWindowInsetBottom());
                return insets;
            }
        });
    }

    public static void setNavBar(final Activity activity, View rootView, final View scrollingView) {
        ViewCompat.setOnApplyWindowInsetsListener(rootView, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                int height = insets.getSystemWindowInsetBottom();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    activity.getWindow().setNavigationBarColor(activity.getColor(
                            height > 100 ? R.color.colorStatusBar : android.R.color.transparent));
                }
                scrollingView.setPadding(
                        scrollingView.getPaddingLeft(),
                        scrollingView.getPaddingTop(),
                        scrollingView.getPaddingRight(),
                        height);
                return insets;
            }
        });
    }

    public static int convertDimension(Context context, @DimenRes int resource){
        Resources r = context.getResources();
        return (int) r.getDimension(resource);
    }

    public static View.OnClickListener buildLinkListener(final Context context, @StringRes final int url){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(context.getString(url));
                context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        };
    }

    public static View.OnClickListener buildEmailListener(final Context context, @StringRes final int email){
        final String address = context.getString(email);
        final String mailto = context.getString(R.string.mail_to);
        final String sendto = context.getString(R.string.send_to);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse(mailto + address));
                context.startActivity(Intent.createChooser(intent, sendto));
            }
        };
    }

    public static int getColor(Context context, int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(color);
        }
        return ContextCompat.getColor(context, color);
    }

}
