package com.hayy.baqala.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import com.hayy.baqala.database.entities.Store;
import java.util.List;

@Dao
public interface StoreDao {

    @Insert
    long insertStore(Store store);

    @Update
    void updateStore(Store store);

    @Delete
    void deleteStore(Store store);

    @Query("SELECT * FROM stores WHERE id = :id")
    Store getStoreById(int id);

    @Query("SELECT * FROM stores")
    List<Store> getAllStores();

    @Query("SELECT * FROM stores WHERE is_open = 1")
    List<Store> getOpenStores();

    @Query("SELECT * FROM stores WHERE delivery_available = 1")
    List<Store> getStoresWithDelivery();

    @Query("SELECT * FROM stores WHERE name LIKE '%' || :query || '%'")
    List<Store> searchStores(String query);

    @Query("DELETE FROM stores")
    void deleteAllStores();
}