package com.hayy.baqala;

import com.hayy.baqala.utils.Constants;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConstantsTest {

    @Test
    public void deliveryFee_isCorrect() {
        assertEquals(10.0, Constants.DELIVERY_FEE, 0.001);
    }

    @Test
    public void minOrderAmount_isCorrect() {
        assertEquals(20.0, Constants.MIN_ORDER_AMOUNT, 0.001);
    }

    @Test
    public void deliveryTypes_areCorrect() {
        assertEquals("pickup", Constants.DELIVERY_PICKUP);
        assertEquals("delivery", Constants.DELIVERY_HOME);
    }

    @Test
    public void deliveryTypes_areDistinct() {
        assertNotEquals(Constants.DELIVERY_PICKUP, Constants.DELIVERY_HOME);
    }

    @Test
    public void orderStatuses_allDefined() {
        assertEquals("pending", Constants.STATUS_PENDING);
        assertEquals("confirmed", Constants.STATUS_CONFIRMED);
        assertEquals("preparing", Constants.STATUS_PREPARING);
        assertEquals("delivering", Constants.STATUS_DELIVERING);
        assertEquals("delivered", Constants.STATUS_DELIVERED);
        assertEquals("cancelled", Constants.STATUS_CANCELLED);
    }

    @Test
    public void orderStatuses_areDistinct() {
        String[] statuses = {
            Constants.STATUS_PENDING,
            Constants.STATUS_CONFIRMED,
            Constants.STATUS_PREPARING,
            Constants.STATUS_DELIVERING,
            Constants.STATUS_DELIVERED,
            Constants.STATUS_CANCELLED
        };
        for (int i = 0; i < statuses.length; i++) {
            for (int j = i + 1; j < statuses.length; j++) {
                assertNotEquals(
                    "Duplicate status: " + statuses[i],
                    statuses[i], statuses[j]
                );
            }
        }
    }

    @Test
    public void loginTypes_areCorrect() {
        assertEquals("phone", Constants.LOGIN_PHONE);
        assertEquals("google", Constants.LOGIN_GOOGLE);
    }

    @Test
    public void loginTypes_areDistinct() {
        assertNotEquals(Constants.LOGIN_PHONE, Constants.LOGIN_GOOGLE);
    }

    @Test
    public void databaseName_isCorrect() {
        assertEquals("hayy_baqala_db", Constants.DATABASE_NAME);
    }

    @Test
    public void paymentMethod_isCorrect() {
        assertEquals("cash", Constants.PAYMENT_CASH);
    }

    @Test
    public void intentKeys_areDistinct() {
        assertNotEquals(Constants.KEY_STORE_ID, Constants.KEY_PRODUCT_ID);
        assertNotEquals(Constants.KEY_STORE_ID, Constants.KEY_ORDER_ID);
        assertNotEquals(Constants.KEY_STORE_ID, Constants.KEY_USER_ID);
        assertNotEquals(Constants.KEY_PRODUCT_ID, Constants.KEY_ORDER_ID);
        assertNotEquals(Constants.KEY_PRODUCT_ID, Constants.KEY_USER_ID);
        assertNotEquals(Constants.KEY_ORDER_ID, Constants.KEY_USER_ID);
    }

    @Test
    public void prefName_isCorrect() {
        assertEquals("HayyBaqalaSession", Constants.PREF_NAME);
    }

    @Test
    public void defaultZoom_isPositive() {
        assertTrue(Constants.DEFAULT_ZOOM > 0);
    }

    @Test
    public void locationPermissionRequest_isPositive() {
        assertTrue(Constants.LOCATION_PERMISSION_REQUEST > 0);
    }
}
