package com.hayy.baqala.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hayy.baqala.R;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.entities.User;
import com.hayy.baqala.databinding.ActivityLoginBinding;
import com.hayy.baqala.ui.home.HomeActivity;
import com.hayy.baqala.utils.Constants;
import com.hayy.baqala.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AppDatabase db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        session = SessionManager.getInstance(this);

        binding.btnLogin.setOnClickListener(v -> loginWithPhone());

        binding.btnGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "تسجيل الدخول بـ Google قريباً", Toast.LENGTH_SHORT).show();
        });

        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }

    private void loginWithPhone() {
        String phone = binding.etPhone.getText().toString().trim();

        if (phone.isEmpty()) {
            binding.tilPhone.setError("أدخل رقم الجوال");
            return;
        }

        if (phone.length() < 10) {
            binding.tilPhone.setError("رقم الجوال غير صحيح");
            return;
        }

        binding.tilPhone.setError(null);
        binding.btnLogin.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        User user = db.userDao().getUserByPhone(phone);

        if (user != null) {
            session.createLoginSession(
                    user.getId(),
                    user.getName(),
                    user.getPhone(),
                    user.getEmail(),
                    Constants.LOGIN_PHONE
            );
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setEnabled(true);
            binding.tilPhone.setError("رقم الجوال غير مسجل، يرجى إنشاء حساب");
        }
    }
}