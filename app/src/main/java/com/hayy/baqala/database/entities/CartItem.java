package com.hayy.baqala.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "cart_items")
public class CartItem {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "user_id")
    private int userId;

    @ColumnInfo(name = "product_id")
    private int productId;

    @ColumnInfo(name = "store_id")
    private int storeId;

    @ColumnInfo(name = "product_name")
    private String productName;

    @ColumnInfo(name = "product_image")
    private String productImage;

    @ColumnInfo(name = "price")
    private double price;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "notes")
    private String notes;

    public CartItem() {}

    public CartItem(int userId, int productId, int storeId, String productName, double price) {
        this.userId = userId;
        this.productId = productId;
        this.storeId = storeId;
        this.productName = productName;
        this.price = price;
        this.quantity = 1;
    }

    public double getTotalPrice() {
        return price * quantity;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getProductId() { return productId; }
    public int getStoreId() { return storeId; }
    public String getProductName() { return productName; }
    public String getProductImage() { return productImage; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setProductId(int productId) { this.productId = productId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setProductImage(String productImage) { this.productImage = productImage; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
