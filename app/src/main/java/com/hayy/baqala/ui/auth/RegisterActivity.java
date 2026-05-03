package com.hayy.baqala.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hayy.baqala.database.entities.User;
import com.hayy.baqala.databinding.ActivityRegisterBinding;
import com.hayy.baqala.ui.home.HomeActivity;
import com.hayy.baqala.utils.FirestoreRepository;
import com.hayy.baqala.utils.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        session = SessionManager.getInstance(this);

        binding.btnRegister.setOnClickListener(v -> register());
        binding.tvLogin.setOnClickListener(v -> finish());
    }

    private void register() {
        String name     = binding.etName.getText().toString().trim();
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString();

        if (name.isEmpty()) {
            binding.tilName.setError("أدخل اسمك الكامل");
            return;
        }
        if (username.isEmpty()) {
            binding.tilUsername.setError("أدخل اسم المستخدم");
            return;
        }
        if (username.length() < 3) {
            binding.tilUsername.setError("اسم المستخدم يجب أن يكون 3 أحرف على الأقل");
            return;
        }
        if (password.isEmpty()) {
            binding.tilPassword.setError("أدخل كلمة المرور");
            return;
        }
        if (password.length() < 6) {
            binding.tilPassword.setError("كلمة المرور 6 أحرف على الأقل");
            return;
        }

        binding.tilName.setError(null);
        binding.tilUsername.setError(null);
        binding.tilPassword.setError(null);
        binding.btnRegister.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        FirestoreRepository.getInstance().getUserByUsername(username, new FirestoreRepository.Callback<User>() {
            @Override
            public void onSuccess(User existing) {
                if (existing != null) {
                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnRegister.setEnabled(true);
                        binding.tilUsername.setError("اسم المستخدم مستخدم مسبقاً");
                    });
                    return;
                }
                FirestoreRepository.getInstance().createUser(name, username, password, new FirestoreRepository.Callback<String>() {
                    @Override
                    public void onSuccess(String firestoreId) {
                        session.createFirestoreLoginSession(firestoreId, name, username);
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.btnRegister.setEnabled(true);
                            Toast.makeText(RegisterActivity.this, "حدث خطأ: " + message, Toast.LENGTH_LONG).show();
                        });
                    }
                });
            }
            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnRegister.setEnabled(true);
                    Toast.makeText(RegisterActivity.this, "حدث خطأ: " + message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
