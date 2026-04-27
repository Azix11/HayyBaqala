package com.hayy.baqala;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.dao.OrderDao;
import com.hayy.baqala.database.entities.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class OrderDaoTest {

    private AppDatabase db;
    private OrderDao orderDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        orderDao = db.orderDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void insertOrder_andGetById() {
        Order order = new Order(1, 1, "Hayy Store", 50.0, "delivery");
        long id = orderDao.insertOrder(order);
        Order retrieved = orderDao.getOrderById((int) id);
        assertNotNull(retrieved);
        assertEquals("Hayy Store", retrieved.getStoreName());
        assertEquals(50.0, retrieved.getTotalPrice(), 0.001);
    }

    @Test
    public void insertOrder_autoGeneratesId() {
        long id1 = orderDao.insertOrder(new Order(1, 1, "Store A", 30.0, "pickup"));
        long id2 = orderDao.insertOrder(new Order(1, 2, "Store B", 40.0, "delivery"));
        assertNotEquals(id1, id2);
    }

    @Test
    public void getOrdersByUser_returnsAllUserOrders() {
        orderDao.insertOrder(new Order(1, 1, "Store A", 30.0, "pickup"));
        orderDao.insertOrder(new Order(1, 2, "Store B", 40.0, "delivery"));
        orderDao.insertOrder(new Order(2, 1, "Store A", 25.0, "pickup"));
        List<Order> user1Orders = orderDao.getOrdersByUser(1);
        assertEquals(2, user1Orders.size());
    }

    @Test
    public void getOrdersByUser_emptyForNewUser() {
        List<Order> orders = orderDao.getOrdersByUser(999);
        assertTrue(orders.isEmpty());
    }

    @Test
    public void getOrdersByUser_orderedByCreatedAtDesc() throws InterruptedException {
        orderDao.insertOrder(new Order(1, 1, "Store A", 30.0, "pickup"));
        Thread.sleep(10);
        orderDao.insertOrder(new Order(1, 2, "Store B", 50.0, "delivery"));
        List<Order> orders = orderDao.getOrdersByUser(1);
        assertTrue(orders.get(0).getCreatedAt() >= orders.get(1).getCreatedAt());
    }

    @Test
    public void getOrdersByStatus_filtersCorrectly() {
        Order pending = new Order(1, 1, "Store A", 30.0, "pickup");
        Order confirmed = new Order(1, 2, "Store B", 40.0, "delivery");
        confirmed.setStatus("confirmed");
        orderDao.insertOrder(pending);
        orderDao.insertOrder(confirmed);
        List<Order> pendingOrders = orderDao.getOrdersByStatus(1, "pending");
        assertEquals(1, pendingOrders.size());
        assertEquals("pending", pendingOrders.get(0).getStatus());
    }

    @Test
    public void getOrdersByStore_returnsStoreOrders() {
        orderDao.insertOrder(new Order(1, 5, "Store X", 30.0, "pickup"));
        orderDao.insertOrder(new Order(2, 5, "Store X", 45.0, "delivery"));
        orderDao.insertOrder(new Order(3, 6, "Store Y", 20.0, "pickup"));
        List<Order> storeXOrders = orderDao.getOrdersByStore(5);
        assertEquals(2, storeXOrders.size());
    }

    @Test
    public void updateOrderStatus_changesStatus() {
        long id = orderDao.insertOrder(new Order(1, 1, "Store A", 30.0, "pickup"));
        orderDao.updateOrderStatus((int) id, "confirmed", System.currentTimeMillis());
        Order updated = orderDao.getOrderById((int) id);
        assertEquals("confirmed", updated.getStatus());
    }

    @Test
    public void getLatestOrder_returnsNewestOrder() throws InterruptedException {
        orderDao.insertOrder(new Order(1, 1, "Store A", 30.0, "pickup"));
        Thread.sleep(10);
        orderDao.insertOrder(new Order(1, 2, "Store B", 55.0, "delivery"));
        Order latest = orderDao.getLatestOrder(1);
        assertNotNull(latest);
        assertEquals("Store B", latest.getStoreName());
    }

    @Test
    public void getLatestOrder_noOrders_returnsNull() {
        assertNull(orderDao.getLatestOrder(999));
    }

    @Test
    public void deleteOrder_removesFromDb() {
        long id = orderDao.insertOrder(new Order(1, 1, "Store A", 30.0, "pickup"));
        Order retrieved = orderDao.getOrderById((int) id);
        orderDao.deleteOrder(retrieved);
        assertNull(orderDao.getOrderById((int) id));
    }

    @Test
    public void deleteAllUserOrders_clearsOnlyThatUser() {
        orderDao.insertOrder(new Order(1, 1, "Store A", 30.0, "pickup"));
        orderDao.insertOrder(new Order(1, 2, "Store B", 40.0, "delivery"));
        orderDao.insertOrder(new Order(2, 1, "Store A", 25.0, "pickup"));
        orderDao.deleteAllUserOrders(1);
        assertTrue(orderDao.getOrdersByUser(1).isEmpty());
        assertEquals(1, orderDao.getOrdersByUser(2).size());
    }

    @Test
    public void updateOrder_changesData() {
        long id = orderDao.insertOrder(new Order(1, 1, "Store A", 30.0, "pickup"));
        Order retrieved = orderDao.getOrderById((int) id);
        retrieved.setDeliveryAddress("123 Test St");
        orderDao.updateOrder(retrieved);
        assertEquals("123 Test St", orderDao.getOrderById((int) id).getDeliveryAddress());
    }
}
