package com.hayy.baqala;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.hayy.baqala.utils.SessionManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class SessionManagerTest {

    private SessionManager sessionManager;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        // Clear shared prefs before each test to ensure isolation
        context.getSharedPreferences("HayyBaqalaSession", Context.MODE_PRIVATE)
               .edit().clear().commit();
        // Reset singleton for clean test state
        try {
            java.lang.reflect.Field instanceField = SessionManager.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            // ignore if reflection fails
        }
        sessionManager = SessionManager.getInstance(context);
    }

    @Test
    public void freshSession_isNotLoggedIn() {
        assertFalse(sessionManager.isLoggedIn());
    }

    @Test
    public void freshSession_userIdIsMinusOne() {
        assertEquals(-1, sessionManager.getUserId());
    }

    @Test
    public void freshSession_userNameIsEmpty() {
        assertEquals("", sessionManager.getUserName());
    }

    @Test
    public void freshSession_userPhoneIsEmpty() {
        assertEquals("", sessionManager.getUserPhone());
    }

    @Test
    public void freshSession_userEmailIsEmpty() {
        assertEquals("", sessionManager.getUserEmail());
    }

    @Test
    public void freshSession_loginTypeIsEmpty() {
        assertEquals("", sessionManager.getLoginType());
    }

    @Test
    public void createLoginSession_setsLoggedInTrue() {
        sessionManager.createLoginSession(1, "Ahmed", "0501234567", "ahmed@test.com", "phone");
        assertTrue(sessionManager.isLoggedIn());
    }

    @Test
    public void createLoginSession_storesUserId() {
        sessionManager.createLoginSession(42, "Ahmed", "0501234567", "ahmed@test.com", "phone");
        assertEquals(42, sessionManager.getUserId());
    }

    @Test
    public void createLoginSession_storesUserName() {
        sessionManager.createLoginSession(1, "Ahmed Al-Rashid", "0501234567", "ahmed@test.com", "phone");
        assertEquals("Ahmed Al-Rashid", sessionManager.getUserName());
    }

    @Test
    public void createLoginSession_storesUserPhone() {
        sessionManager.createLoginSession(1, "Ahmed", "0501234567", "ahmed@test.com", "phone");
        assertEquals("0501234567", sessionManager.getUserPhone());
    }

    @Test
    public void createLoginSession_storesUserEmail() {
        sessionManager.createLoginSession(1, "Ahmed", "0501234567", "ahmed@test.com", "phone");
        assertEquals("ahmed@test.com", sessionManager.getUserEmail());
    }

    @Test
    public void createLoginSession_storesLoginType() {
        sessionManager.createLoginSession(1, "Ahmed", "0501234567", "ahmed@test.com", "google");
        assertEquals("google", sessionManager.getLoginType());
    }

    @Test
    public void logout_setsLoggedInFalse() {
        sessionManager.createLoginSession(1, "Ahmed", "0501234567", "ahmed@test.com", "phone");
        sessionManager.logout();
        assertFalse(sessionManager.isLoggedIn());
    }

    @Test
    public void logout_clearsUserId() {
        sessionManager.createLoginSession(1, "Ahmed", "0501234567", "ahmed@test.com", "phone");
        sessionManager.logout();
        assertEquals(-1, sessionManager.getUserId());
    }

    @Test
    public void logout_clearsUserName() {
        sessionManager.createLoginSession(1, "Ahmed", "0501234567", "ahmed@test.com", "phone");
        sessionManager.logout();
        assertEquals("", sessionManager.getUserName());
    }

    @Test
    public void logout_clearsAllData() {
        sessionManager.createLoginSession(1, "Ahmed", "0501234567", "ahmed@test.com", "phone");
        sessionManager.logout();
        assertFalse(sessionManager.isLoggedIn());
        assertEquals(-1, sessionManager.getUserId());
        assertEquals("", sessionManager.getUserName());
        assertEquals("", sessionManager.getUserPhone());
        assertEquals("", sessionManager.getUserEmail());
        assertEquals("", sessionManager.getLoginType());
    }

    @Test
    public void updateUserName_changesStoredName() {
        sessionManager.createLoginSession(1, "Ahmed", "0501234567", "ahmed@test.com", "phone");
        sessionManager.updateUserName("Mohammed");
        assertEquals("Mohammed", sessionManager.getUserName());
    }

    @Test
    public void updateUserName_doesNotAffectOtherFields() {
        sessionManager.createLoginSession(1, "Ahmed", "0501234567", "ahmed@test.com", "phone");
        sessionManager.updateUserName("Mohammed");
        assertEquals(1, sessionManager.getUserId());
        assertEquals("0501234567", sessionManager.getUserPhone());
        assertEquals("ahmed@test.com", sessionManager.getUserEmail());
        assertTrue(sessionManager.isLoggedIn());
    }

    @Test
    public void getInstance_returnsSameInstance() {
        Context context = ApplicationProvider.getApplicationContext();
        SessionManager instance1 = SessionManager.getInstance(context);
        SessionManager instance2 = SessionManager.getInstance(context);
        assertSame(instance1, instance2);
    }

    @Test
    public void loginAfterLogout_resetsSession() {
        sessionManager.createLoginSession(1, "Ahmed", "0501234567", "ahmed@test.com", "phone");
        sessionManager.logout();
        sessionManager.createLoginSession(2, "Sara", "0559999999", "sara@test.com", "google");
        assertTrue(sessionManager.isLoggedIn());
        assertEquals(2, sessionManager.getUserId());
        assertEquals("Sara", sessionManager.getUserName());
    }
}
