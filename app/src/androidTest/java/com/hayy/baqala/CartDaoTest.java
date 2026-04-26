package com.hayy.baqala;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.dao.CartDao;
import com.hayy.baqala.database.entities.CartItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class CartDaoTest {

    private AppDatabase db;
    private CartDao cartDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        cartDao = db.cartDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void insertCartItem_andRetrieve() {
        cartDao.insertCartItem(new CartItem(1, 10, 5, "Milk", 5.0));
        List<CartItem> items = cartDao.getCartItems(1);
        assertEquals(1, items.size());
        assertEquals("Milk", items.get(0).getProductName());
    }

    @Test
    public void getCartItems_onlyReturnsItemsForRequestedUser() {
        cartDao.insertCartItem(new CartItem(1, 10, 5, "Milk", 5.0));
        cartDao.insertCartItem(new CartItem(2, 11, 5, "Bread", 3.0));
        List<CartItem> user1Items = cartDao.getCartItems(1);
        assertEquals(1, user1Items.size());
        assertEquals("Milk", user1Items.get(0).getProductName());
    }

    @Test
    public void getCartItemCount_correct() {
        cartDao.insertCartItem(new CartItem(1, 10, 5, "Milk", 5.0));
        cartDao.insertCartItem(new CartItem(1, 11, 5, "Bread", 3.0));
        assertEquals(2, cartDao.getCartItemCount(1));
    }

    @Test
    public void getCartItemCount_emptyCart_returnsZero() {
        assertEquals(0, cartDao.getCartItemCount(999));
    }

    @Test
    public void getCartTotal_calculatesCorrectly() {
        CartItem item1 = new CartItem(1, 10, 5, "Milk", 5.0);
        item1.setQuantity(2); // 10.0
        CartItem item2 = new CartItem(1, 11, 5, "Bread", 3.0);
        item2.setQuantity(1); // 3.0
        cartDao.insertCartItem(item1);
        cartDao.insertCartItem(item2);
        assertEquals(13.0, cartDao.getCartTotal(1), 0.001);
    }

    @Test
    public void getCartTotal_emptyCart_returnsZero() {
        assertEquals(0.0, cartDao.getCartTotal(999), 0.001);
    }

    @Test
    public void getCartTotal_singleItemMultipleQuantity() {
        CartItem item = new CartItem(1, 10, 5, "Water", 2.5);
        item.setQuantity(6);
        cartDao.insertCartItem(item);
        assertEquals(15.0, cartDao.getCartTotal(1), 0.001);
    }

    @Test
    public void getCartItemByProduct_returnsCorrectItem() {
        cartDao.insertCartItem(new CartItem(1, 10, 5, "Milk", 5.0));
        CartItem found = cartDao.getCartItemByProduct(1, 10);
        assertNotNull(found);
        assertEquals("Milk", found.getProductName());
    }

    @Test
    public void getCartItemByProduct_notFound_returnsNull() {
        assertNull(cartDao.getCartItemByProduct(1, 999));
    }

    @Test
    public void updateCartItem_changesQuantity() {
        cartDao.insertCartItem(new CartItem(1, 10, 5, "Milk", 5.0));
        CartItem retrieved = cartDao.getCartItems(1).get(0);
        retrieved.setQuantity(5);
        cartDao.updateCartItem(retrieved);
        assertEquals(5, cartDao.getCartItems(1).get(0).getQuantity());
    }

    @Test
    public void deleteCartItem_removesItem() {
        cartDao.insertCartItem(new CartItem(1, 10, 5, "Milk", 5.0));
        CartItem retrieved = cartDao.getCartItems(1).get(0);
        cartDao.deleteCartItem(retrieved);
        assertTrue(cartDao.getCartItems(1).isEmpty());
    }

    @Test
    public void deleteCartItemById_removesItem() {
        long id = cartDao.insertCartItem(new CartItem(1, 10, 5, "Milk", 5.0));
        cartDao.deleteCartItemById((int) id);
        assertTrue(cartDao.getCartItems(1).isEmpty());
    }

    @Test
    public void clearCart_removesAllItemsForUser() {
        cartDao.insertCartItem(new CartItem(1, 10, 5, "Milk", 5.0));
        cartDao.insertCartItem(new CartItem(1, 11, 5, "Bread", 3.0));
        cartDao.clearCart(1);
        assertEquals(0, cartDao.getCartItemCount(1));
    }

    @Test
    public void clearCart_doesNotAffectOtherUsers() {
        cartDao.insertCartItem(new CartItem(1, 10, 5, "Milk", 5.0));
        cartDao.insertCartItem(new CartItem(2, 11, 5, "Eggs", 8.0));
        cartDao.clearCart(1);
        assertEquals(0, cartDao.getCartItemCount(1));
        assertEquals(1, cartDao.getCartItemCount(2));
    }
}
