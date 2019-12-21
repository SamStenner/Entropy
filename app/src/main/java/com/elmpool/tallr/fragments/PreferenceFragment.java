package com.elmpool.tallr.fragments;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elmpool.tallr.R;
import com.elmpool.tallr.activities.SettingsActivity;
import com.elmpool.tallr.utils.Manager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Arrays;

public class PreferenceFragment extends PreferenceFragmentCompat {

    private static final int THEME_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    private static final int THEME_BATTERY = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
    private static final int THEME_LIGHT = AppCompatDelegate.MODE_NIGHT_NO;
    private static final int THEME_DARK = AppCompatDelegate.MODE_NIGHT_YES;
    private static final int THEME_UNKNOWN = AppCompatDelegate.MODE_NIGHT_UNSPECIFIED;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        setThemePref();
        setupLogoutPref();
    }

    private void setupLogoutPref(){
        Preference preference = findPreference(getString(R.string.key_logout));
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext(), R.style.MaterialAlertDialog_Rounded)
                        .setTitle(getString(R.string.lbl_confirm))
                        .setPositiveButton(getString(R.string.lbl_yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth.getInstance().signOut();
                                ((SettingsActivity)getActivity()).logout();
                            }
                        })
                        .setNegativeButton(getString(R.string.lbl_cancel), null);
                builder.show();
                return false;
            }
        });
    }

    private void setThemePref() {
        final ListPreference listPreference = handleVersion();
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String pref = newValue.toString();
                int index = listPreference.findIndexOfValue(pref);
                preference.setSummary(listPreference.getEntries()[index].toString());
                int value = Integer.valueOf(listPreference.getEntryValues()[index].toString());
                AppCompatDelegate.setDefaultNightMode(value);
                return true;
            }
        };
        listPreference.setOnPreferenceChangeListener(listener);
        listener.onPreferenceChange(listPreference,
                PreferenceManager.getDefaultSharedPreferences(listPreference.getContext())
                        .getString(listPreference.getKey(), getString(R.string.theme_default)));
    }

    private ListPreference handleVersion(){
        ListPreference preference = findPreference(getString(R.string.key_theme));

        ArrayList<String> listValues = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.theme_option_values)));
        ArrayList<String> listEntries = new ArrayList<>(
                Arrays.asList(getResources().getStringArray(R.array.theme_option_titles)));

        int mode = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ? THEME_BATTERY : THEME_SYSTEM;
        int index = preference.findIndexOfValue(String.valueOf(mode));

        listValues.remove(index);
        listEntries.remove(index);

        String[] values = new String[listValues.size()];
        String[] entries = new String[listEntries.size()];

        listValues.toArray(values);
        listEntries.toArray(entries);

        preference.setEntryValues(values);
        preference.setEntries(entries);

        return preference;
    }

    @Override
    public RecyclerView onCreateRecyclerView (LayoutInflater inflater, ViewGroup parent, Bundle state) {
        RecyclerView recyclerView = super.onCreateRecyclerView(inflater, parent, state);

        recyclerView.setPadding(
                recyclerView.getPaddingLeft(),
                Manager.convertDimension(getContext(), R.dimen.toolbar_padding),
                recyclerView.getRight(),
                recyclerView.getPaddingBottom());
        ((SettingsActivity)getActivity()).setupRecyclerView(recyclerView);
        return recyclerView;
    }

}
