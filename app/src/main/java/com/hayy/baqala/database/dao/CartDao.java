package com.hayy.baqala.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import com.hayy.baqala.database.entities.CartItem;
import java.util.List;

@Dao
public interface CartDao {

    @Insert
    long insertCartItem(CartItem cartItem);

    @Update
    void updateCartItem(CartItem cartItem);

    @Delete
    void deleteCartItem(CartItem cartItem);

    @Query("SELECT * FROM cart_items WHERE user_id = :userId")
    List<CartItem> getCartItems(int userId);

    @Query("SELECT * FROM cart_items WHERE user_id = :userId AND product_id = :productId LIMIT 1")
    CartItem getCartItemByProduct(int userId, int productId);

    @Query("SELECT COUNT(*) FROM cart_items WHERE user_id = :userId")
    int getCartItemCount(int userId);

    @Query("SELECT SUM(price * quantity) FROM cart_items WHERE user_id = :userId")
    double getCartTotal(int userId);

    @Query("DELETE FROM cart_items WHERE user_id = :userId")
    void clearCart(int userId);

    @Query("DELETE FROM cart_items WHERE id = :itemId")
    void deleteCartItemById(int itemId);
}