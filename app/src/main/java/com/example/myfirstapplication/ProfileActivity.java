package com.example.myfirstapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvUsername;
    private Button btnLogout, btnBack, btnOrderHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SystemBars.setup(this, R.id.heroContent, true);
        NavigationHelper.setupBottomNav(this);

        tvUsername = findViewById(R.id.tvProfileUsername);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);
        btnOrderHistory = findViewById(R.id.btnOrderHistory);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = prefs.getString("DISPLAY_NAME", getString(R.string.profile_unknown));
        tvUsername.setText(getString(R.string.profile_hello, username));

        btnBack.setOnClickListener(v -> finish());

        btnOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
