package com.hayy.baqala.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

public class LocationHelper {

    public interface LocationCallback {
        void onLocation(double latitude, double longitude);
        void onError(String message);
    }

    public static void getCurrentLocation(Context context, LocationCallback callback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            callback.onError("لا يوجد إذن للوصول للموقع");
            return;
        }

        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);

        // Try last known location first (fast)
        client.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                callback.onLocation(location.getLatitude(), location.getLongitude());
            } else {
                // Request fresh location
                CancellationTokenSource cts = new CancellationTokenSource();
                client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.getToken())
                        .addOnSuccessListener(fresh -> {
                            if (fresh != null) {
                                callback.onLocation(fresh.getLatitude(), fresh.getLongitude());
                            } else {
                                // Default to Riyadh center if location unavailable
                                callback.onLocation(24.7136, 46.6753);
                            }
                        })
                        .addOnFailureListener(e -> callback.onLocation(24.7136, 46.6753));
            }
        }).addOnFailureListener(e -> callback.onLocation(24.7136, 46.6753));
    }

    public static float distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        float[] result = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, result);
        return result[0];
    }

    public static String formatDistance(float meters) {
        if (meters < 1000) {
            return Math.round(meters) + " م";
        } else {
            return String.format("%.1f كم", meters / 1000f);
        }
    }

    /** Estimate delivery time: 10 min prep + travel at 35 km/h average */
    public static int estimateDeliveryMinutes(float meters) {
        float km = meters / 1000f;
        int travelMin = (int) Math.ceil((km / 35f) * 60);
        return 10 + travelMin;
    }
}
