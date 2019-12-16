package com.elmpool.tallr.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elmpool.tallr.R;
import com.elmpool.tallr.activities.SettingsActivity;
import com.elmpool.tallr.widgets.BounceRecyclerAdapter;
import com.elmpool.tallr.widgets.BounceRecyclerView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;

public class PreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setThemePref(findPreference(getString(R.string.key_theme)));
        setupLogoutPref(findPreference(getString(R.string.key_logout)));
    }

    private void setupLogoutPref(Preference preference){
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialAlertDialogBuilder(getContext(), R.style.MaterialAlertDialog_Rounded)
                        .setTitle(getString(R.string.lbl_confirm))
                        .setPositiveButton(getString(R.string.lbl_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                ((SettingsActivity)getActivity()).logout();
                            }
                        })
                        .setNegativeButton(getString(R.string.lbl_cancel), null)
                        .show();

                return false;
            }
        });
    }

    private void setThemePref(Preference preference) {
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String pref = newValue.toString();
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(pref);
                preference.setSummary(listPreference.getEntries()[index].toString());
                int value = Integer.valueOf(listPreference.getEntryValues()[index].toString());
                AppCompatDelegate.setDefaultNightMode(value);
                return true;
            }
        };
        preference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), "-1"));
    }

    @Override
    public RecyclerView onCreateRecyclerView (LayoutInflater inflater, ViewGroup parent, Bundle state) {
        RecyclerView recyclerView = super.onCreateRecyclerView(inflater, parent, state);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        BounceRecyclerAdapter decorAdapter = new BounceRecyclerAdapter(recyclerView);
        new VerticalOverScrollBounceEffectDecorator(decorAdapter,
                BounceRecyclerView.ratioFwd,
                BounceRecyclerView.ratioBck,
                BounceRecyclerView.decelFac);
        return recyclerView;
    }

}
