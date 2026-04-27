package com.hayy.baqala;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.dao.ProductDao;
import com.hayy.baqala.database.entities.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class ProductDaoTest {

    private AppDatabase db;
    private ProductDao productDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        productDao = db.productDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void insertProduct_andGetById() {
        Product product = new Product(1, "Milk", 5.0, "Dairy");
        long id = productDao.insertProduct(product);
        Product retrieved = productDao.getProductById((int) id);
        assertNotNull(retrieved);
        assertEquals("Milk", retrieved.getName());
        assertEquals(5.0, retrieved.getPrice(), 0.001);
        assertEquals("Dairy", retrieved.getCategory());
    }

    @Test
    public void getProductById_nonExistent_returnsNull() {
        assertNull(productDao.getProductById(999));
    }

    @Test
    public void getProductsByStore_returnsOnlyStoreProducts() {
        productDao.insertProduct(new Product(1, "Milk", 5.0, "Dairy"));
        productDao.insertProduct(new Product(1, "Bread", 3.0, "Bakery"));
        productDao.insertProduct(new Product(2, "Juice", 7.0, "Beverages"));
        List<Product> store1Products = productDao.getProductsByStore(1);
        assertEquals(2, store1Products.size());
    }

    @Test
    public void getProductsByStore_emptyStore_returnsEmptyList() {
        assertTrue(productDao.getProductsByStore(999).isEmpty());
    }

    @Test
    public void getProductsByCategory_filtersCorrectly() {
        productDao.insertProduct(new Product(1, "Milk", 5.0, "Dairy"));
        productDao.insertProduct(new Product(1, "Cheese", 12.0, "Dairy"));
        productDao.insertProduct(new Product(1, "Bread", 3.0, "Bakery"));
        List<Product> dairyProducts = productDao.getProductsByCategory(1, "Dairy");
        assertEquals(2, dairyProducts.size());
    }

    @Test
    public void getAvailableProducts_excludesUnavailable() {
        Product available = new Product(1, "Milk", 5.0, "Dairy");
        Product unavailable = new Product(1, "OldBread", 1.0, "Bakery");
        unavailable.setAvailable(false);
        productDao.insertProduct(available);
        productDao.insertProduct(unavailable);
        List<Product> availableProducts = productDao.getAvailableProducts(1);
        assertEquals(1, availableProducts.size());
        assertEquals("Milk", availableProducts.get(0).getName());
    }

    @Test
    public void searchProducts_findsMatchingName() {
        productDao.insertProduct(new Product(1, "Full Fat Milk", 5.0, "Dairy"));
        productDao.insertProduct(new Product(1, "Skimmed Milk", 4.5, "Dairy"));
        productDao.insertProduct(new Product(1, "Bread", 3.0, "Bakery"));
        List<Product> results = productDao.searchProducts("Milk");
        assertEquals(2, results.size());
    }

    @Test
    public void searchProducts_caseInsensitiveMatch() {
        productDao.insertProduct(new Product(1, "Milk", 5.0, "Dairy"));
        List<Product> results = productDao.searchProducts("milk");
        assertEquals(1, results.size());
    }

    @Test
    public void searchProducts_noMatch_returnsEmptyList() {
        productDao.insertProduct(new Product(1, "Milk", 5.0, "Dairy"));
        assertTrue(productDao.searchProducts("xyz_no_match").isEmpty());
    }

    @Test
    public void getCategoriesByStore_returnsDistinctCategories() {
        productDao.insertProduct(new Product(1, "Milk", 5.0, "Dairy"));
        productDao.insertProduct(new Product(1, "Cheese", 12.0, "Dairy"));
        productDao.insertProduct(new Product(1, "Bread", 3.0, "Bakery"));
        List<String> categories = productDao.getCategoriesByStore(1);
        assertEquals(2, categories.size());
        assertTrue(categories.contains("Dairy"));
        assertTrue(categories.contains("Bakery"));
    }

    @Test
    public void insertProducts_bulkInsert() {
        List<Product> products = Arrays.asList(
            new Product(1, "Milk", 5.0, "Dairy"),
            new Product(1, "Bread", 3.0, "Bakery"),
            new Product(1, "Juice", 7.0, "Beverages")
        );
        productDao.insertProducts(products);
        assertEquals(3, productDao.getProductsByStore(1).size());
    }

    @Test
    public void updateProduct_changesData() {
        long id = productDao.insertProduct(new Product(1, "Milk", 5.0, "Dairy"));
        Product retrieved = productDao.getProductById((int) id);
        retrieved.setPrice(6.0);
        productDao.updateProduct(retrieved);
        assertEquals(6.0, productDao.getProductById((int) id).getPrice(), 0.001);
    }

    @Test
    public void deleteProduct_removesFromDb() {
        long id = productDao.insertProduct(new Product(1, "Milk", 5.0, "Dairy"));
        Product retrieved = productDao.getProductById((int) id);
        productDao.deleteProduct(retrieved);
        assertNull(productDao.getProductById((int) id));
    }

    @Test
    public void deleteProductsByStore_removesOnlyStoreProducts() {
        productDao.insertProduct(new Product(1, "Milk", 5.0, "Dairy"));
        productDao.insertProduct(new Product(1, "Bread", 3.0, "Bakery"));
        productDao.insertProduct(new Product(2, "Juice", 7.0, "Beverages"));
        productDao.deleteProductsByStore(1);
        assertTrue(productDao.getProductsByStore(1).isEmpty());
        assertEquals(1, productDao.getProductsByStore(2).size());
    }
}
