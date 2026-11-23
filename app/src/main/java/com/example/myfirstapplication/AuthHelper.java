package com.example.myfirstapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Patterns;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class AuthHelper {
    static final String PREF_NAME = "UserPrefs";
    static final String KEY_LOGGED_USER = "LOGGED_USER";
    static final String KEY_DISPLAY_NAME = "DISPLAY_NAME";

    static void saveSession(Context ctx, FirebaseUser user) {
        String email = user.getEmail() != null ? user.getEmail() : "";
        String name = user.getDisplayName() != null && !user.getDisplayName().isEmpty()
                ? user.getDisplayName() : email;
        ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
                .putBoolean("IS_LOGGED_IN", true)
                .putString(KEY_LOGGED_USER, email)
                .putString(KEY_DISPLAY_NAME, name)
                .apply();
    }

    static void openMain(android.app.Activity from) {
        Intent intent = new Intent(from, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        from.startActivity(intent);
    }

    static String validEmail(TextInputLayout layout, String raw) {
        String email = raw == null ? "" : raw.trim();
        boolean ok = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        layout.setError(ok ? null : layout.getContext().getString(R.string.err_email_invalid));
        return ok ? email : null;
    }

    static boolean validPassword(TextInputLayout layout, String pass) {
        boolean ok = pass != null && pass.length() >= 6;
        layout.setError(ok ? null : layout.getContext().getString(R.string.err_password_short));
        return ok;
    }
}
