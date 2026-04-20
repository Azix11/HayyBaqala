package com.hayy.baqala.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import com.hayy.baqala.database.entities.Product;
import java.util.List;

@Dao
public interface ProductDao {

    @Insert
    long insertProduct(Product product);

    @Insert
    void insertProducts(List<Product> products);

    @Update
    void updateProduct(Product product);

    @Delete
    void deleteProduct(Product product);

    @Query("SELECT * FROM products WHERE id = :id")
    Product getProductById(int id);

    @Query("SELECT * FROM products WHERE store_id = :storeId")
    List<Product> getProductsByStore(int storeId);

    @Query("SELECT * FROM products WHERE store_id = :storeId AND category = :category")
    List<Product> getProductsByCategory(int storeId, String category);

    @Query("SELECT * FROM products WHERE store_id = :storeId AND is_available = 1")
    List<Product> getAvailableProducts(int storeId);

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%'")
    List<Product> searchProducts(String query);

    @Query("SELECT DISTINCT category FROM products WHERE store_id = :storeId")
    List<String> getCategoriesByStore(int storeId);

    @Query("DELETE FROM products WHERE store_id = :storeId")
    void deleteProductsByStore(int storeId);
}