package com.example.myfirstapplication;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences prefs = getSharedPreferences(SettingsActivity.PREF_NAME, MODE_PRIVATE);
        if (prefs.contains(SettingsActivity.KEY_DARK)) {
            AppCompatDelegate.setDefaultNightMode(prefs.getBoolean(SettingsActivity.KEY_DARK, false)
                    ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
