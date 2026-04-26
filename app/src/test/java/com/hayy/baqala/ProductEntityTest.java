package com.hayy.baqala;

import com.hayy.baqala.database.entities.Product;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProductEntityTest {

    @Test
    public void constructor_defaultsIsAvailableTrue() {
        Product product = new Product(1, "Milk", 5.0, "Dairy");
        assertTrue(product.isAvailable());
    }

    @Test
    public void constructor_defaultsStockTo100() {
        Product product = new Product(1, "Milk", 5.0, "Dairy");
        assertEquals(100, product.getStock());
    }

    @Test
    public void constructor_setsAllFields() {
        Product product = new Product(2, "Bread", 3.5, "Bakery");
        assertEquals(2, product.getStoreId());
        assertEquals("Bread", product.getName());
        assertEquals(3.5, product.getPrice(), 0.001);
        assertEquals("Bakery", product.getCategory());
    }

    @Test
    public void setAvailable_toFalse() {
        Product product = new Product(1, "Milk", 5.0, "Dairy");
        product.setAvailable(false);
        assertFalse(product.isAvailable());
    }

    @Test
    public void setAvailable_backToTrue() {
        Product product = new Product(1, "Milk", 5.0, "Dairy");
        product.setAvailable(false);
        product.setAvailable(true);
        assertTrue(product.isAvailable());
    }

    @Test
    public void setStock_updatesValue() {
        Product product = new Product(1, "Milk", 5.0, "Dairy");
        product.setStock(50);
        assertEquals(50, product.getStock());
    }

    @Test
    public void setStock_toZero() {
        Product product = new Product(1, "Milk", 5.0, "Dairy");
        product.setStock(0);
        assertEquals(0, product.getStock());
    }

    @Test
    public void setPrice_updatesValue() {
        Product product = new Product(1, "Milk", 5.0, "Dairy");
        product.setPrice(7.5);
        assertEquals(7.5, product.getPrice(), 0.001);
    }

    @Test
    public void setDescription_storesValue() {
        Product product = new Product(1, "Milk", 5.0, "Dairy");
        product.setDescription("Fresh full-fat milk");
        assertEquals("Fresh full-fat milk", product.getDescription());
    }

    @Test
    public void setUnit_storesValue() {
        Product product = new Product(1, "Milk", 5.0, "Dairy");
        product.setUnit("Litre");
        assertEquals("Litre", product.getUnit());
    }

    @Test
    public void newProduct_descriptionIsNull() {
        Product product = new Product(1, "Milk", 5.0, "Dairy");
        assertNull(product.getDescription());
    }

    @Test
    public void defaultConstructor_worksForRoom() {
        Product product = new Product();
        assertNotNull(product);
    }
}
