package com.hayy.baqala.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import com.hayy.baqala.database.entities.Order;
import java.util.List;

@Dao
public interface OrderDao {

    @Insert
    long insertOrder(Order order);

    @Update
    void updateOrder(Order order);

    @Delete
    void deleteOrder(Order order);

    @Query("SELECT * FROM orders WHERE id = :id")
    Order getOrderById(int id);

    @Query("SELECT * FROM orders WHERE user_id = :userId ORDER BY created_at DESC")
    List<Order> getOrdersByUser(int userId);

    @Query("SELECT * FROM orders WHERE user_id = :userId AND status = :status ORDER BY created_at DESC")
    List<Order> getOrdersByStatus(int userId, String status);

    @Query("SELECT * FROM orders WHERE store_id = :storeId ORDER BY created_at DESC")
    List<Order> getOrdersByStore(int storeId);

    @Query("UPDATE orders SET status = :status, updated_at = :updatedAt WHERE id = :orderId")
    void updateOrderStatus(int orderId, String status, long updatedAt);

    @Query("SELECT * FROM orders WHERE user_id = :userId ORDER BY created_at DESC LIMIT 1")
    Order getLatestOrder(int userId);

    @Query("DELETE FROM orders WHERE user_id = :userId")
    void deleteAllUserOrders(int userId);
}