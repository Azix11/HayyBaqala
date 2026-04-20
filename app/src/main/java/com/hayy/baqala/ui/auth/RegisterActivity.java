package com.hayy.baqala.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.entities.User;
import com.hayy.baqala.databinding.ActivityRegisterBinding;
import com.hayy.baqala.ui.home.HomeActivity;
import com.hayy.baqala.utils.Constants;
import com.hayy.baqala.utils.SessionManager;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AppDatabase db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        session = SessionManager.getInstance(this);

        binding.btnRegister.setOnClickListener(v -> register());

        binding.tvLogin.setOnClickListener(v -> {
            finish();
        });
    }

    private void register() {
        String name = binding.etName.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();

        if (name.isEmpty()) {
            binding.tilName.setError("أدخل اسمك الكامل");
            return;
        }

        if (phone.isEmpty()) {
            binding.tilPhone.setError("أدخل رقم الجوال");
            return;
        }

        if (phone.length() < 10) {
            binding.tilPhone.setError("رقم الجوال غير صحيح");
            return;
        }

        binding.tilName.setError(null);
        binding.tilPhone.setError(null);
        binding.btnRegister.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        User existingUser = db.userDao().getUserByPhone(phone);
        if (existingUser != null) {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnRegister.setEnabled(true);
            binding.tilPhone.setError("رقم الجوال مسجل مسبقاً");
            return;
        }

        User newUser = new User(name, phone, "");
        long userId = db.userDao().insertUser(newUser);

        session.createLoginSession(
                (int) userId,
                name,
                phone,
                "",
                Constants.LOGIN_PHONE
        );

        startActivity(new Intent(this, HomeActivity.class));
        finishAffinity();
    }
}