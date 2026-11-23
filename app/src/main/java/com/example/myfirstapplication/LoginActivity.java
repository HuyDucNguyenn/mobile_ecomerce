package com.example.myfirstapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText edtEmail, edtPassword;
    private MaterialButton btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FirebaseUser current = auth.getCurrentUser();
        if (current != null) {
            AuthHelper.saveSession(this, current);
            AuthHelper.openMain(this);
            return;
        }

        setContentView(R.layout.activity_login);

        SystemBars.setup(this, R.id.heroContent, true);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        tilEmail = (TextInputLayout) edtEmail.getParent().getParent();
        tilPassword = (TextInputLayout) edtPassword.getParent().getParent();
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> login());
        findViewById(R.id.btnToRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
        findViewById(R.id.btnForgot).setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    private void login() {
        String email = AuthHelper.validEmail(tilEmail, String.valueOf(edtEmail.getText()));
        String pass = String.valueOf(edtPassword.getText());
        boolean passOk = !pass.isEmpty();
        tilPassword.setError(passOk ? null : getString(R.string.err_password_short));
        if (email == null || !passOk) return;

        btnLogin.setEnabled(false);
        auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(result -> {
                    AuthHelper.saveSession(this, result.getUser());
                    AuthHelper.openMain(this);
                })
                .addOnFailureListener(e -> {
                    btnLogin.setEnabled(true);
                    boolean badCredentials = e instanceof FirebaseAuthInvalidCredentialsException
                            || e instanceof FirebaseAuthInvalidUserException;
                    Toast.makeText(this, getString(badCredentials ? R.string.login_error : R.string.err_network),
                            Toast.LENGTH_SHORT).show();
                });
    }
}
