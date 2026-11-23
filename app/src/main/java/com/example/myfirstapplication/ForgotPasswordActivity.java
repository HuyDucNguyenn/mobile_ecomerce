package com.example.myfirstapplication;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        SystemBars.setup(this, R.id.heroContent, true);

        TextInputEditText edtEmail = findViewById(R.id.edtEmail);
        TextInputLayout tilEmail = (TextInputLayout) edtEmail.getParent().getParent();
        MaterialButton btnSend = findViewById(R.id.btnSend);

        findViewById(R.id.btnBackToLogin).setOnClickListener(v -> finish());
        btnSend.setOnClickListener(v -> {
            String email = AuthHelper.validEmail(tilEmail, String.valueOf(edtEmail.getText()));
            if (email == null) return;

            btnSend.setEnabled(false);
            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener(r -> {
                        Toast.makeText(this, getString(R.string.forgot_sent), Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnSend.setEnabled(true);
                        Toast.makeText(this, getString(R.string.err_network), Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
