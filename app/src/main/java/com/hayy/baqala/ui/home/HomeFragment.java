package com.hayy.baqala.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.hayy.baqala.database.entities.Store;
import com.hayy.baqala.databinding.FragmentHomeBinding;
import com.hayy.baqala.utils.FirestoreRepository;
import com.hayy.baqala.utils.LocationHelper;
import com.hayy.baqala.utils.SessionManager;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private SessionManager session;

    private static final int LOCATION_PERMISSION_CODE = 1001;
    private double userLat = 0, userLon = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        session = SessionManager.getInstance(requireContext());

        String userName = session.getUserName();
        binding.tvWelcome.setText(!userName.isEmpty() ? "أهلاً، " + userName + " 👋" : "أهلاً 👋");

        binding.etSearch.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SearchActivity.class)));

        requestLocationAndLoadStores();
    }

    private void requestLocationAndLoadStores() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
        } else {
            fetchLocationThenStores();
        }
    }

    private void fetchLocationThenStores() {
        LocationHelper.getCurrentLocation(requireContext(), new LocationHelper.LocationCallback() {
            @Override
            public void onLocation(double lat, double lon) {
                userLat = lat;
                userLon = lon;
                loadStores();
            }
            @Override
            public void onError(String message) {
                loadStores();
            }
        });
    }

    private void loadStores() {
        FirestoreRepository.getInstance().getStores(new FirestoreRepository.Callback<List<Store>>() {
            @Override
            public void onSuccess(List<Store> stores) {
                if (binding == null) return;
                if (stores != null && !stores.isEmpty()) {
                    StoreAdapter adapter = new StoreAdapter(requireContext(), stores, userLat, userLon);
                    binding.rvStores.setLayoutManager(new LinearLayoutManager(requireContext()));
                    binding.rvStores.setAdapter(adapter);
                } else {
                    Toast.makeText(requireContext(), "لا توجد متاجر متاحة", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onError(String message) {
                if (binding == null) return;
                Toast.makeText(requireContext(), "خطأ في تحميل المتاجر", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationThenStores();
            } else {
                loadStores();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
