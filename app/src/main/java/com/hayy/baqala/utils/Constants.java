package com.hayy.baqala.utils;

public class Constants {

    // Database
    public static final String DATABASE_NAME = "hayy_baqala_db";

    // Login Types
    public static final String LOGIN_PHONE = "phone";
    public static final String LOGIN_GOOGLE = "google";

    // Delivery Types
    public static final String DELIVERY_PICKUP = "pickup";
    public static final String DELIVERY_HOME = "delivery";

    // Order Status
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_CONFIRMED = "confirmed";
    public static final String STATUS_PREPARING = "preparing";
    public static final String STATUS_DELIVERING = "delivering";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_CANCELLED = "cancelled";

    // Payment
    public static final String PAYMENT_CASH = "cash";

    // Intent Keys
    public static final String KEY_STORE_ID = "store_id";
    public static final String KEY_PRODUCT_ID = "product_id";
    public static final String KEY_ORDER_ID = "order_id";
    public static final String KEY_USER_ID = "user_id";

    // Delivery Fee
    public static final double DELIVERY_FEE = 10.0;
    public static final double MIN_ORDER_AMOUNT = 20.0;

    // Shared Preferences
    public static final String PREF_NAME = "HayyBaqalaSession";

    // Map
    public static final float DEFAULT_ZOOM = 14f;
    public static final int LOCATION_PERMISSION_REQUEST = 1001;
}