package com.hayy.baqala.ui.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hayy.baqala.R;
import com.hayy.baqala.database.entities.Store;
import com.hayy.baqala.databinding.FragmentMapBinding;
import com.hayy.baqala.utils.Constants;
import com.hayy.baqala.utils.FirestoreRepository;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private FragmentMapBinding binding;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        binding.btnMyLocation.setOnClickListener(v -> getCurrentLocation());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        checkLocationPermission();
        loadStoresOnMap();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            getCurrentLocation();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.LOCATION_PERMISSION_REQUEST);
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null && mMap != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, Constants.DEFAULT_ZOOM));
            }
        });
    }

    private void loadStoresOnMap() {
        // تحريك الكاميرا للرياض أولاً بينما تُحمَّل البيانات
        LatLng riyadh = new LatLng(24.7136, 46.6753);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(riyadh, Constants.DEFAULT_ZOOM));

        FirestoreRepository.getInstance().getStores(new FirestoreRepository.Callback<List<Store>>() {
            @Override
            public void onSuccess(List<Store> stores) {
                if (stores == null || mMap == null) return;

                for (Store store : stores) {
                    if (store.getLatitude() == 0 && store.getLongitude() == 0) continue;

                    LatLng storeLocation = new LatLng(store.getLatitude(), store.getLongitude());
                    String snippet = store.isOpen() ? "مفتوح الآن" : "مغلق";
                    if (store.isDeliveryAvailable()) snippet += " • توصيل متاح";

                    float markerColor = store.isOpen()
                            ? BitmapDescriptorFactory.HUE_GREEN
                            : BitmapDescriptorFactory.HUE_RED;

                    mMap.addMarker(new MarkerOptions()
                            .position(storeLocation)
                            .title(store.getName())
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(markerColor)));
                }
            }

            @Override
            public void onError(String message) {}
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    getCurrentLocation();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
