package com.hayy.baqala;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.dao.StoreDao;
import com.hayy.baqala.database.entities.Store;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class StoreDaoTest {

    private AppDatabase db;
    private StoreDao storeDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        storeDao = db.storeDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void insertStore_andGetById() {
        Store store = new Store("Hayy Baqala", "Riyadh", 24.7136, 46.6753);
        long id = storeDao.insertStore(store);
        Store retrieved = storeDao.getStoreById((int) id);
        assertNotNull(retrieved);
        assertEquals("Hayy Baqala", retrieved.getName());
        assertEquals("Riyadh", retrieved.getAddress());
    }

    @Test
    public void getStoreById_nonExistent_returnsNull() {
        assertNull(storeDao.getStoreById(999));
    }

    @Test
    public void getAllStores_returnsAll() {
        storeDao.insertStore(new Store("Store A", "Riyadh", 24.7136, 46.6753));
        storeDao.insertStore(new Store("Store B", "Jeddah", 21.4858, 39.1925));
        storeDao.insertStore(new Store("Store C", "Dammam", 26.3927, 49.9777));
        assertEquals(3, storeDao.getAllStores().size());
    }

    @Test
    public void getAllStores_emptyDb_returnsEmptyList() {
        assertTrue(storeDao.getAllStores().isEmpty());
    }

    @Test
    public void getOpenStores_returnsOnlyOpenStores() {
        Store open = new Store("Open Store", "Riyadh", 24.7136, 46.6753);
        Store closed = new Store("Closed Store", "Riyadh", 24.7200, 46.6800);
        closed.setOpen(false);
        storeDao.insertStore(open);
        storeDao.insertStore(closed);
        List<Store> openStores = storeDao.getOpenStores();
        assertEquals(1, openStores.size());
        assertEquals("Open Store", openStores.get(0).getName());
    }

    @Test
    public void getStoresWithDelivery_returnsOnlyDeliveryStores() {
        Store withDelivery = new Store("Delivery Store", "Riyadh", 24.7136, 46.6753);
        Store pickupOnly = new Store("Pickup Only", "Riyadh", 24.7200, 46.6800);
        pickupOnly.setDeliveryAvailable(false);
        storeDao.insertStore(withDelivery);
        storeDao.insertStore(pickupOnly);
        List<Store> deliveryStores = storeDao.getStoresWithDelivery();
        assertEquals(1, deliveryStores.size());
        assertEquals("Delivery Store", deliveryStores.get(0).getName());
    }

    @Test
    public void searchStores_findsByName() {
        storeDao.insertStore(new Store("Hayy Baqala", "Riyadh", 24.7136, 46.6753));
        storeDao.insertStore(new Store("Fresh Market", "Riyadh", 24.7200, 46.6800));
        storeDao.insertStore(new Store("Hayy Express", "Jeddah", 21.4858, 39.1925));
        List<Store> results = storeDao.searchStores("Hayy");
        assertEquals(2, results.size());
    }

    @Test
    public void searchStores_noMatch_returnsEmptyList() {
        storeDao.insertStore(new Store("Hayy Baqala", "Riyadh", 24.7136, 46.6753));
        assertTrue(storeDao.searchStores("xyz_no_match").isEmpty());
    }

    @Test
    public void updateStore_changesData() {
        long id = storeDao.insertStore(new Store("Old Name", "Riyadh", 24.7136, 46.6753));
        Store retrieved = storeDao.getStoreById((int) id);
        retrieved.setName("New Name");
        storeDao.updateStore(retrieved);
        assertEquals("New Name", storeDao.getStoreById((int) id).getName());
    }

    @Test
    public void deleteStore_removesFromDb() {
        long id = storeDao.insertStore(new Store("Test Store", "Riyadh", 24.7136, 46.6753));
        Store retrieved = storeDao.getStoreById((int) id);
        storeDao.deleteStore(retrieved);
        assertNull(storeDao.getStoreById((int) id));
    }

    @Test
    public void deleteAllStores_clearsTable() {
        storeDao.insertStore(new Store("Store A", "Riyadh", 24.7136, 46.6753));
        storeDao.insertStore(new Store("Store B", "Jeddah", 21.4858, 39.1925));
        storeDao.deleteAllStores();
        assertTrue(storeDao.getAllStores().isEmpty());
    }

    @Test
    public void insertStore_autoGeneratesId() {
        long id1 = storeDao.insertStore(new Store("Store A", "Riyadh", 24.7136, 46.6753));
        long id2 = storeDao.insertStore(new Store("Store B", "Jeddah", 21.4858, 39.1925));
        assertNotEquals(id1, id2);
    }
}
