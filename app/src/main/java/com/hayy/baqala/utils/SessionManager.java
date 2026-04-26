package com.hayy.baqala.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String PREF_NAME = "HayyBaqalaSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_LOGIN_TYPE = "loginType";
    private static final String KEY_IS_ADMIN = "isAdmin";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static SessionManager instance;

    private SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    public void createLoginSession(int userId, String name, String phone, String email, String loginType) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_PHONE, phone);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_LOGIN_TYPE, loginType);
        editor.putBoolean(KEY_IS_ADMIN, Constants.STORE_ADMIN_PHONE.equals(phone));
        editor.apply();
    }

    public boolean isAdmin() {
        return pref.getBoolean(KEY_IS_ADMIN, false);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, "");
    }

    public String getUserPhone() {
        return pref.getString(KEY_USER_PHONE, "");
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, "");
    }

    public String getLoginType() {
        return pref.getString(KEY_LOGIN_TYPE, "");
    }

    public void updateUserName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}