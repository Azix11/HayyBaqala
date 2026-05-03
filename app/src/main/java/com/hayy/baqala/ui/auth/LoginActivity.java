package com.hayy.baqala.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hayy.baqala.R;
import com.hayy.baqala.database.entities.User;
import com.hayy.baqala.databinding.ActivityLoginBinding;
import com.hayy.baqala.ui.home.HomeActivity;
import com.hayy.baqala.utils.Constants;
import com.hayy.baqala.utils.FirestoreRepository;
import com.hayy.baqala.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private SessionManager session;
    private boolean isAdminMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = SessionManager.getInstance(this);

        binding.btnLogin.setOnClickListener(v -> {
            if (isAdminMode) loginAsAdmin();
            else loginAsUser();
        });

        binding.tvAdminToggle.setOnClickListener(v -> toggleAdminMode());

        binding.btnGoogle.setOnClickListener(v ->
                Toast.makeText(this, "تسجيل الدخول بـ Google قريباً", Toast.LENGTH_SHORT).show());

        binding.tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void toggleAdminMode() {
        isAdminMode = !isAdminMode;
        if (isAdminMode) {
            binding.tilPhone.setVisibility(View.GONE);
            binding.tilUsername.setVisibility(View.VISIBLE);
            binding.tilPassword.setVisibility(View.VISIBLE);
            binding.tvAdminToggle.setText(R.string.user_login);
            binding.tilPhone.setError(null);
        } else {
            binding.tilPhone.setVisibility(View.VISIBLE);
            binding.tilUsername.setVisibility(View.GONE);
            binding.tilPassword.setVisibility(View.GONE);
            binding.tvAdminToggle.setText(R.string.admin_login);
            binding.tilUsername.setError(null);
            binding.tilPassword.setError(null);
        }
    }

    /** Regular user login: username + password via Firestore. Auto-create if not found. */
    private void loginAsUser() {
        String username = binding.etPhone.getText().toString().trim();
        String password = "";

        // Phone field is reused as username in user mode
        if (username.isEmpty()) {
            binding.tilPhone.setError("أدخل اسم المستخدم");
            return;
        }

        binding.tilPhone.setError(null);
        binding.btnLogin.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        // We need the password from the password field, but in user mode it's hidden.
        // In this mode the phone field acts as username only - no password required for regular
        // users (they just enter username and get auto-login / auto-register).
        // If password field is visible (not admin mode), use it.
        if (binding.tilPassword.getVisibility() == View.VISIBLE) {
            password = binding.etPassword.getText().toString();
        }

        final String finalPassword = password;

        // If password field is empty, do username-only lookup or auto-create
        if (finalPassword.isEmpty()) {
            FirestoreRepository.getInstance().getUserByUsername(username, new FirestoreRepository.Callback<User>() {
                @Override
                public void onSuccess(User user) {
                    if (user != null) {
                        session.createFirestoreLoginSession(user.firestoreId, user.getName(), user.getUsername());
                        navigateToHome();
                    } else {
                        // Auto-create account
                        FirestoreRepository.getInstance().createUser(username, username, "", new FirestoreRepository.Callback<String>() {
                            @Override
                            public void onSuccess(String fsId) {
                                session.createFirestoreLoginSession(fsId, username, username);
                                navigateToHome();
                            }
                            @Override
                            public void onError(String msg) { showError(msg); }
                        });
                    }
                }
                @Override
                public void onError(String msg) { showError(msg); }
            });
        } else {
            FirestoreRepository.getInstance().getUserByCredentials(username, finalPassword, new FirestoreRepository.Callback<User>() {
                @Override
                public void onSuccess(User user) {
                    if (user != null) {
                        session.createFirestoreLoginSession(user.firestoreId, user.getName(), user.getUsername());
                        navigateToHome();
                    } else {
                        runOnUiThread(() -> {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.btnLogin.setEnabled(true);
                            binding.tilPhone.setError("اسم المستخدم أو كلمة المرور غير صحيحة");
                        });
                    }
                }
                @Override
                public void onError(String msg) { showError(msg); }
            });
        }
    }

    /** Admin login: hardcoded admin/admin credentials */
    private void loginAsAdmin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString();

        if (username.isEmpty()) {
            binding.tilUsername.setError("أدخل اسم المستخدم");
            return;
        }
        if (password.isEmpty()) {
            binding.tilPassword.setError("أدخل كلمة المرور");
            return;
        }

        binding.tilUsername.setError(null);
        binding.tilPassword.setError(null);

        if (Constants.ADMIN_USERNAME.equals(username) && Constants.ADMIN_PASSWORD.equals(password)) {
            session.createAdminLoginSession(username);
            navigateToHome();
        } else {
            binding.tilPassword.setError("اسم المستخدم أو كلمة المرور غير صحيحة");
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showError(String msg) {
        runOnUiThread(() -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setEnabled(true);
            Toast.makeText(this, "حدث خطأ: " + msg, Toast.LENGTH_LONG).show();
        });
    }
}
