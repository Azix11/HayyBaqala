package com.hayy.baqala;

import com.hayy.baqala.database.entities.CartItem;
import org.junit.Test;
import static org.junit.Assert.*;

public class CartItemEntityTest {

    @Test
    public void getTotalPrice_singleQuantity() {
        CartItem item = new CartItem(1, 1, 1, "Milk", 5.0);
        assertEquals(5.0, item.getTotalPrice(), 0.001);
    }

    @Test
    public void getTotalPrice_multipleQuantity() {
        CartItem item = new CartItem(1, 1, 1, "Milk", 5.0);
        item.setQuantity(3);
        assertEquals(15.0, item.getTotalPrice(), 0.001);
    }

    @Test
    public void getTotalPrice_zeroQuantity() {
        CartItem item = new CartItem(1, 1, 1, "Milk", 5.0);
        item.setQuantity(0);
        assertEquals(0.0, item.getTotalPrice(), 0.001);
    }

    @Test
    public void getTotalPrice_fractionalPrice() {
        CartItem item = new CartItem(1, 1, 1, "Apple", 2.5);
        item.setQuantity(4);
        assertEquals(10.0, item.getTotalPrice(), 0.001);
    }

    @Test
    public void getTotalPrice_largeQuantity() {
        CartItem item = new CartItem(1, 1, 1, "Rice", 25.0);
        item.setQuantity(10);
        assertEquals(250.0, item.getTotalPrice(), 0.001);
    }

    @Test
    public void defaultConstructor_quantityIsOne() {
        CartItem item = new CartItem(1, 2, 3, "Bread", 3.0);
        assertEquals(1, item.getQuantity());
    }

    @Test
    public void constructor_setsAllFields() {
        CartItem item = new CartItem(1, 2, 3, "Bread", 3.0);
        assertEquals(1, item.getUserId());
        assertEquals(2, item.getProductId());
        assertEquals(3, item.getStoreId());
        assertEquals("Bread", item.getProductName());
        assertEquals(3.0, item.getPrice(), 0.001);
    }

    @Test
    public void setQuantity_updatesValue() {
        CartItem item = new CartItem(1, 1, 1, "Milk", 5.0);
        item.setQuantity(7);
        assertEquals(7, item.getQuantity());
    }

    @Test
    public void setPrice_updatesValue() {
        CartItem item = new CartItem(1, 1, 1, "Milk", 5.0);
        item.setPrice(6.5);
        assertEquals(6.5, item.getPrice(), 0.001);
    }

    @Test
    public void setProductImage_storesValue() {
        CartItem item = new CartItem(1, 1, 1, "Milk", 5.0);
        item.setProductImage("milk.png");
        assertEquals("milk.png", item.getProductImage());
    }

    @Test
    public void defaultConstructor_worksForRoom() {
        CartItem item = new CartItem();
        assertNotNull(item);
    }
}
