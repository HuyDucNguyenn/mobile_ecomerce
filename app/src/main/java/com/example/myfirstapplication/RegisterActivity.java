package com.example.myfirstapplication;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirm;
    private TextInputEditText edtName, edtEmail, edtPassword, edtConfirm;
    private MaterialButton btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SystemBars.setup(this, R.id.heroContent, true);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirm = findViewById(R.id.edtConfirm);
        tilName = (TextInputLayout) edtName.getParent().getParent();
        tilEmail = (TextInputLayout) edtEmail.getParent().getParent();
        tilPassword = (TextInputLayout) edtPassword.getParent().getParent();
        tilConfirm = (TextInputLayout) edtConfirm.getParent().getParent();
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> register());
        findViewById(R.id.btnToLogin).setOnClickListener(v -> finish());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void register() {
        String name = String.valueOf(edtName.getText()).trim();
        tilName.setError(name.isEmpty() ? getString(R.string.err_name_required) : null);
        String email = AuthHelper.validEmail(tilEmail, String.valueOf(edtEmail.getText()));
        String pass = String.valueOf(edtPassword.getText());
        boolean passOk = AuthHelper.validPassword(tilPassword, pass);
        boolean same = pass.equals(String.valueOf(edtConfirm.getText()));
        tilConfirm.setError(same ? null : getString(R.string.err_password_mismatch));
        if (name.isEmpty() || email == null || !passOk || !same) return;

        btnRegister.setEnabled(false);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(result -> {
                    FirebaseUser user = result.getUser();
                    user.updateProfile(new UserProfileChangeRequest.Builder().setDisplayName(name).build())
                            .addOnCompleteListener(t -> {
                                Map<String, Object> profile = new HashMap<>();
                                profile.put("name", name);
                                profile.put("email", email);
                                profile.put("createdAt", Timestamp.now());
                                FirebaseFirestore.getInstance().collection("users").document(user.getUid()).set(profile);

                                AuthHelper.saveSession(this, user);
                                Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show();
                                AuthHelper.openMain(this);
                            });
                })
                .addOnFailureListener(e -> {
                    btnRegister.setEnabled(true);
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        tilEmail.setError(getString(R.string.err_email_taken));
                    } else {
                        Toast.makeText(this, getString(R.string.register_failed, e.getLocalizedMessage()),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
