package com.hayy.baqala.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "stores")
public class Store {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "image")
    public String image;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    @ColumnInfo(name = "rating")
    public float rating;

    @ColumnInfo(name = "is_open")
    public boolean isOpen;

    @ColumnInfo(name = "opening_time")
    public String openingTime;

    @ColumnInfo(name = "closing_time")
    public String closingTime;

    @ColumnInfo(name = "delivery_available")
    public boolean deliveryAvailable;

    @ColumnInfo(name = "delivery_fee")
    public double deliveryFee;

    @ColumnInfo(name = "min_order")
    public double minOrder;

    public Store() {}

    public Store(String name, String address, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isOpen = true;
        this.deliveryAvailable = true;
        this.deliveryFee = 10.0;
        this.minOrder = 20.0;
        this.rating = 4.0f;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public String getImage() { return image; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public float getRating() { return rating; }
    public boolean isOpen() { return isOpen; }
    public String getOpeningTime() { return openingTime; }
    public String getClosingTime() { return closingTime; }
    public boolean isDeliveryAvailable() { return deliveryAvailable; }
    public double getDeliveryFee() { return deliveryFee; }
    public double getMinOrder() { return minOrder; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setImage(String image) { this.image = image; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setRating(float rating) { this.rating = rating; }
    public void setOpen(boolean open) { isOpen = open; }
    public void setOpeningTime(String openingTime) { this.openingTime = openingTime; }
    public void setClosingTime(String closingTime) { this.closingTime = closingTime; }
    public void setDeliveryAvailable(boolean deliveryAvailable) { this.deliveryAvailable = deliveryAvailable; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }
    public void setMinOrder(double minOrder) { this.minOrder = minOrder; }
}