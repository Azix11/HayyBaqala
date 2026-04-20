package com.hayy.baqala.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "products")
public class Product {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "store_id")
    public int storeId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "price")
    public double price;

    @ColumnInfo(name = "image")
    public String image;

    @ColumnInfo(name = "category")
    public String category;

    @ColumnInfo(name = "unit")
    public String unit;

    @ColumnInfo(name = "stock")
    public int stock;

    @ColumnInfo(name = "is_available")
    public boolean isAvailable;

    public Product() {}

    public Product(int storeId, String name, double price, String category) {
        this.storeId = storeId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.isAvailable = true;
        this.stock = 100;
    }

    public int getId() { return id; }
    public int getStoreId() { return storeId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImage() { return image; }
    public String getCategory() { return category; }
    public String getUnit() { return unit; }
    public int getStock() { return stock; }
    public boolean isAvailable() { return isAvailable; }

    public void setId(int id) { this.id = id; }
    public void setStoreId(int storeId) { this.storeId = storeId; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setImage(String image) { this.image = image; }
    public void setCategory(String category) { this.category = category; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setStock(int stock) { this.stock = stock; }
    public void setAvailable(boolean available) { isAvailable = available; }
}