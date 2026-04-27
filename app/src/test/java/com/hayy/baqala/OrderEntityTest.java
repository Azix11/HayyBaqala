package com.hayy.baqala;

import com.hayy.baqala.database.entities.Order;
import org.junit.Test;
import static org.junit.Assert.*;

public class OrderEntityTest {

    @Test
    public void deliveryOrder_setsDeliveryFee() {
        Order order = new Order(1, 1, "Test Store", 50.0, "delivery");
        assertEquals(10.0, order.getDeliveryFee(), 0.001);
    }

    @Test
    public void pickupOrder_zeroDeliveryFee() {
        Order order = new Order(1, 1, "Test Store", 50.0, "pickup");
        assertEquals(0.0, order.getDeliveryFee(), 0.001);
    }

    @Test
    public void deliveryOrder_grandTotalIncludesDeliveryFee() {
        Order order = new Order(1, 1, "Test Store", 50.0, "delivery");
        assertEquals(60.0, order.getGrandTotal(), 0.001);
    }

    @Test
    public void pickupOrder_grandTotalEqualsItemTotal() {
        Order order = new Order(1, 1, "Test Store", 50.0, "pickup");
        assertEquals(50.0, order.getGrandTotal(), 0.001);
    }

    @Test
    public void newOrder_defaultStatusIsPending() {
        Order order = new Order(1, 1, "Test Store", 50.0, "delivery");
        assertEquals("pending", order.getStatus());
    }

    @Test
    public void newOrder_defaultPaymentIsCash() {
        Order order = new Order(1, 1, "Test Store", 50.0, "delivery");
        assertEquals("cash", order.getPaymentMethod());
    }

    @Test
    public void newOrder_timestampsAreSet() {
        long before = System.currentTimeMillis();
        Order order = new Order(1, 1, "Test Store", 50.0, "delivery");
        long after = System.currentTimeMillis();
        assertTrue(order.getCreatedAt() >= before && order.getCreatedAt() <= after);
        assertTrue(order.getUpdatedAt() >= before && order.getUpdatedAt() <= after);
    }

    @Test
    public void setStatus_updatesUpdatedAt() throws InterruptedException {
        Order order = new Order(1, 1, "Test Store", 50.0, "delivery");
        long initialUpdatedAt = order.getUpdatedAt();
        Thread.sleep(10);
        order.setStatus("confirmed");
        assertTrue(order.getUpdatedAt() >= initialUpdatedAt);
        assertEquals("confirmed", order.getStatus());
    }

    @Test
    public void zeroTotalPrice_deliveryGrandTotalIsOnlyFee() {
        Order order = new Order(1, 1, "Test Store", 0.0, "delivery");
        assertEquals(10.0, order.getGrandTotal(), 0.001);
    }

    @Test
    public void zeroTotalPrice_pickupGrandTotalIsZero() {
        Order order = new Order(1, 1, "Test Store", 0.0, "pickup");
        assertEquals(0.0, order.getGrandTotal(), 0.001);
    }

    @Test
    public void constructor_setsAllFields() {
        Order order = new Order(5, 3, "My Store", 75.0, "pickup");
        assertEquals(5, order.getUserId());
        assertEquals(3, order.getStoreId());
        assertEquals("My Store", order.getStoreName());
        assertEquals(75.0, order.getTotalPrice(), 0.001);
        assertEquals("pickup", order.getDeliveryType());
    }

    @Test
    public void largeTotalPrice_grandTotalCalculatedCorrectly() {
        Order order = new Order(1, 1, "Test Store", 999.99, "delivery");
        assertEquals(1009.99, order.getGrandTotal(), 0.001);
    }

    @Test
    public void defaultConstructor_worksForRoom() {
        Order order = new Order();
        assertNotNull(order);
    }

    @Test
    public void setDeliveryAddress_storesValue() {
        Order order = new Order(1, 1, "Test Store", 50.0, "delivery");
        order.setDeliveryAddress("123 Main St, Riyadh");
        assertEquals("123 Main St, Riyadh", order.getDeliveryAddress());
    }

    @Test
    public void setNotes_storesValue() {
        Order order = new Order(1, 1, "Test Store", 50.0, "delivery");
        order.setNotes("Please ring the doorbell");
        assertEquals("Please ring the doorbell", order.getNotes());
    }
}
