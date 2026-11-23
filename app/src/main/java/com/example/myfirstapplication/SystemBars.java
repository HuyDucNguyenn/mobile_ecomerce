package com.example.myfirstapplication;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

final class SystemBars {
    private SystemBars() { }

    @SuppressWarnings("deprecation")
    static void setup(Activity a, int topViewId, boolean onHero) {
        View root = a.findViewById(android.R.id.content);
        View top = a.findViewById(topViewId);
        final int basePad = top.getPaddingTop();
        final View glass = a.findViewById(R.id.bottomNavCard);
        final int baseMargin = glass == null ? 0 : ((android.view.ViewGroup.MarginLayoutParams) glass.getLayoutParams()).bottomMargin;
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            top.setPadding(top.getPaddingLeft(), basePad + insets.getSystemWindowInsetTop(), top.getPaddingRight(), top.getPaddingBottom());
            if (glass != null) {
                android.view.ViewGroup.MarginLayoutParams lp = (android.view.ViewGroup.MarginLayoutParams) glass.getLayoutParams();
                lp.bottomMargin = baseMargin + insets.getSystemWindowInsetBottom();
                glass.setLayoutParams(lp);
                v.setPadding(0, 0, 0, 0);
            } else {
                v.setPadding(0, 0, 0, insets.getSystemWindowInsetBottom());
            }
            return WindowInsetsCompat.CONSUMED;
        });
        boolean night = (a.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
        WindowCompat.getInsetsController(a.getWindow(), root).setAppearanceLightStatusBars(!onHero && !night);
    }
}
