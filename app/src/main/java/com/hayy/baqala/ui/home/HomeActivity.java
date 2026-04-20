package com.hayy.baqala.ui.home;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.hayy.baqala.R;
import com.hayy.baqala.databinding.ActivityHomeBinding;
import com.hayy.baqala.ui.cart.CartFragment;
import com.hayy.baqala.ui.map.MapFragment;
import com.hayy.baqala.ui.orders.OrdersFragment;
import com.hayy.baqala.ui.profile.ProfileFragment;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // الشاشة الافتراضية
        loadFragment(new HomeFragment());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                loadFragment(new HomeFragment());
            } else if (id == R.id.nav_map) {
                loadFragment(new MapFragment());
            } else if (id == R.id.nav_cart) {
                loadFragment(new CartFragment());
            } else if (id == R.id.nav_orders) {
                loadFragment(new OrdersFragment());
            } else if (id == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
            }
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}