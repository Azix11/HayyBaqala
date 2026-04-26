package com.hayy.baqala;

import com.hayy.baqala.database.entities.Store;
import org.junit.Test;
import static org.junit.Assert.*;

public class StoreEntityTest {

    @Test
    public void constructor_defaultsIsOpenTrue() {
        Store store = new Store("Hayy Store", "Riyadh", 24.7136, 46.6753);
        assertTrue(store.isOpen());
    }

    @Test
    public void constructor_defaultsDeliveryAvailableTrue() {
        Store store = new Store("Hayy Store", "Riyadh", 24.7136, 46.6753);
        assertTrue(store.isDeliveryAvailable());
    }

    @Test
    public void constructor_defaultDeliveryFeeIsTen() {
        Store store = new Store("Hayy Store", "Riyadh", 24.7136, 46.6753);
        assertEquals(10.0, store.getDeliveryFee(), 0.001);
    }

    @Test
    public void constructor_defaultMinOrderIsTwenty() {
        Store store = new Store("Hayy Store", "Riyadh", 24.7136, 46.6753);
        assertEquals(20.0, store.getMinOrder(), 0.001);
    }

    @Test
    public void constructor_defaultRatingIsFour() {
        Store store = new Store("Hayy Store", "Riyadh", 24.7136, 46.6753);
        assertEquals(4.0f, store.getRating(), 0.001f);
    }

    @Test
    public void constructor_setsNameAndAddress() {
        Store store = new Store("Hayy Store", "Riyadh", 24.7136, 46.6753);
        assertEquals("Hayy Store", store.getName());
        assertEquals("Riyadh", store.getAddress());
    }

    @Test
    public void constructor_setsCoordinates() {
        Store store = new Store("Test Store", "Riyadh", 24.7136, 46.6753);
        assertEquals(24.7136, store.getLatitude(), 0.0001);
        assertEquals(46.6753, store.getLongitude(), 0.0001);
    }

    @Test
    public void setOpen_toFalse() {
        Store store = new Store("Test Store", "Riyadh", 24.7136, 46.6753);
        store.setOpen(false);
        assertFalse(store.isOpen());
    }

    @Test
    public void setDeliveryAvailable_toFalse() {
        Store store = new Store("Test Store", "Riyadh", 24.7136, 46.6753);
        store.setDeliveryAvailable(false);
        assertFalse(store.isDeliveryAvailable());
    }

    @Test
    public void setRating_updatesValue() {
        Store store = new Store("Test Store", "Riyadh", 24.7136, 46.6753);
        store.setRating(4.8f);
        assertEquals(4.8f, store.getRating(), 0.001f);
    }

    @Test
    public void setDeliveryFee_updatesValue() {
        Store store = new Store("Test Store", "Riyadh", 24.7136, 46.6753);
        store.setDeliveryFee(15.0);
        assertEquals(15.0, store.getDeliveryFee(), 0.001);
    }

    @Test
    public void setMinOrder_updatesValue() {
        Store store = new Store("Test Store", "Riyadh", 24.7136, 46.6753);
        store.setMinOrder(50.0);
        assertEquals(50.0, store.getMinOrder(), 0.001);
    }

    @Test
    public void setOpeningAndClosingTime_storesValues() {
        Store store = new Store("Test Store", "Riyadh", 24.7136, 46.6753);
        store.setOpeningTime("08:00");
        store.setClosingTime("22:00");
        assertEquals("08:00", store.getOpeningTime());
        assertEquals("22:00", store.getClosingTime());
    }

    @Test
    public void setPhone_storesValue() {
        Store store = new Store("Test Store", "Riyadh", 24.7136, 46.6753);
        store.setPhone("0112345678");
        assertEquals("0112345678", store.getPhone());
    }

    @Test
    public void defaultConstructor_worksForRoom() {
        Store store = new Store();
        assertNotNull(store);
    }
}
