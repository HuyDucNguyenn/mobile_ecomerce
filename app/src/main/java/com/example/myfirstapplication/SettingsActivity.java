package com.example.myfirstapplication;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import com.google.android.material.materialswitch.MaterialSwitch;

public class SettingsActivity extends AppCompatActivity {
    static final String PREF_NAME = "AppSettings";
    static final String KEY_DARK = "DARK_MODE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SystemBars.setup(this, R.id.heroContent, false);
        NavigationHelper.setupBottomNav(this);

        RadioGroup rgLanguage = findViewById(R.id.rgLanguage);
        MaterialSwitch switchDark = findViewById(R.id.switchDark);

        boolean isEnglish = "en".equals(getResources().getConfiguration().getLocales().get(0).getLanguage());
        rgLanguage.check(isEnglish ? R.id.rbEnglish : R.id.rbVietnamese);
        boolean isDark = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
        switchDark.setChecked(isDark);

        rgLanguage.setOnCheckedChangeListener((group, id) ->
                AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(id == R.id.rbEnglish ? "en" : "vi")));

        switchDark.setOnCheckedChangeListener((button, checked) -> {
            getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit().putBoolean(KEY_DARK, checked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    checked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });
    }
}
