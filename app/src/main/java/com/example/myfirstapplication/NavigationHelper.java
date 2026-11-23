package com.example.myfirstapplication;

import android.app.Activity;
import android.view.View;
import android.widget.AbsListView;
import android.content.Intent;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationHelper {

    public static void setupBottomNav(Activity activity) {
        BottomNavigationView bottomNav = activity.findViewById(R.id.bottom_navigation);
        if (bottomNav == null) return;

        GlassLayout glass = activity.findViewById(R.id.bottomNavCard);
        View source = activity.findViewById(R.id.glassSource);
        if (glass != null && source != null) {
            glass.setBlurSource(source);
            View list = activity.findViewById(R.id.listView);
            if (list == null) list = activity.findViewById(R.id.listViewOrders);
            if (list instanceof AbsListView) glass.trackScroll((AbsListView) list);
        }

        if (activity instanceof MainActivity) {
            bottomNav.setSelectedItemId(R.id.navHome);
        } else if (activity instanceof CartActivity) {
            bottomNav.setSelectedItemId(R.id.navCart);
        } else if (activity instanceof OrderHistoryActivity) {
            bottomNav.setSelectedItemId(R.id.navHistory);
        } else if (activity instanceof ProfileActivity) {
            bottomNav.setSelectedItemId(R.id.navProfile);
        } else if (activity instanceof SettingsActivity) {
            bottomNav.setSelectedItemId(R.id.navSettings);
        }

        if (activity instanceof LifecycleOwner) {
            ((LifecycleOwner) activity).getLifecycle().addObserver(new DefaultLifecycleObserver() {
                @Override public void onResume(@NonNull LifecycleOwner owner) { refreshCartBadge(activity); }
            });
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navHome) {
                if (!(activity instanceof MainActivity)) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    if (!(activity instanceof MainActivity)) activity.finish();
                    return false;
                }
                return true;
            } else if (itemId == R.id.navCart) {
                if (!(activity instanceof CartActivity)) {
                    Intent intent = new Intent(activity, CartActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    if (!(activity instanceof MainActivity)) activity.finish();
                    return false;
                }
                return true;
            } else if (itemId == R.id.navHistory) {
                if (!(activity instanceof OrderHistoryActivity)) {
                    Intent intent = new Intent(activity, OrderHistoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    if (!(activity instanceof MainActivity)) activity.finish();
                    return false;
                }
                return true;
            } else if (itemId == R.id.navProfile) {
                if (!(activity instanceof ProfileActivity)) {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    if (!(activity instanceof MainActivity)) activity.finish();
                    return false;
                }
                return true;
            } else if (itemId == R.id.navSettings) {
                if (!(activity instanceof SettingsActivity)) {
                    Intent intent = new Intent(activity, SettingsActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    if (!(activity instanceof MainActivity)) activity.finish();
                    return false;
                }
                return true;
            }
            return false;
        });
    }

    public static void refreshCartBadge(Activity activity) {
        BottomNavigationView nav = activity.findViewById(R.id.bottom_navigation);
        if (nav == null) return;
        int total = 0;
        Cursor c = new CartDatabaseHelper(activity).getAllCartItems();
        if (c != null) {
            while (c.moveToNext()) total += c.getInt(c.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_QUANTITY));
            c.close();
        }
        if (total > 0) {
            BadgeDrawable badge = nav.getOrCreateBadge(R.id.navCart);
            badge.setNumber(total);
            badge.setBackgroundColor(MaterialColors.getColor(nav, com.google.android.material.R.attr.colorPrimary));
            badge.setBadgeTextColor(MaterialColors.getColor(nav, com.google.android.material.R.attr.colorOnPrimary));
        } else {
            nav.removeBadge(R.id.navCart);
        }
    }
}
