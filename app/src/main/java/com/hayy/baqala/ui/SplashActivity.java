package com.hayy.baqala.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.hayy.baqala.R;
import com.hayy.baqala.ui.auth.LoginActivity;
import com.hayy.baqala.ui.home.HomeActivity;
import com.hayy.baqala.utils.DatabaseSeeder;
import com.hayy.baqala.utils.SessionManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // تهيئة البيانات التجريبية
        DatabaseSeeder.seedIfEmpty(this);

        // الانتقال بعد 2 ثانية
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager session = SessionManager.getInstance(this);
            Intent intent;
            if (session.isLoggedIn()) {
                intent = new Intent(this, HomeActivity.class);
            } else {
                intent = new Intent(this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        }, 2000);
    }
}