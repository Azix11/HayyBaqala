package com.hayy.baqala.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "orders")
public class Order {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "user_id")
    public int userId;

    @ColumnInfo(name = "store_id")
    public int storeId;

    @ColumnInfo(name = "store_name")
    public String storeName;

    @ColumnInfo(name = "total_price")
    public double totalPrice;

    @ColumnInfo(name = "delivery_fee")
    public double deliveryFee;

    @ColumnInfo(name = "grand_total")
    public double grandTotal;

    @ColumnInfo(name = "delivery_type")
    // "pickup" or "delivery"
    public String deliveryType;

    @ColumnInfo(name = "delivery_address")
    public String deliveryAddress;

    @ColumnInfo(name = "status")
    // pending, confirmed, preparing, delivering, delivered, cancelled
    public String status;

    @ColumnInfo(name = "payment_method")
    public String paymentMethod;

    @ColumnInfo(name = "notes")
    public String notes;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "updated_at")
    public long updatedAt;

    public Order() {}

    public Order(int userId, int storeId, String storeName, double totalPrice, String deliveryType) {
        this.userId = userId;
        this.storeId = storeId;
        this.storeName = storeName;
        this.totalPrice = totalPrice;
        this.deliveryType = deliveryType;
        this.status = "pending";
        this.paymentMethod = "cash";
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();

        if (deliveryType.equals("delivery")) {
            this.deliveryFee = 10.0;
        } else {
            this.deliveryFee = 0.0;
        }
        this.grandTotal = totalPrice + this.deliveryFee;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getStoreId() { return storeId; }
    public String getStoreName() { return storeName; }
    public double getTotalPrice() { return totalPrice; }
    public double getDeliveryFee() { return deliveryFee; }
    public double getGrandTotal() { return grandTotal; }
    public String getDeliveryType() { return deliveryType; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getNotes() { return notes; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setDeliveryFee(double deliveryFee) { this.deliveryFee = deliveryFee; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }
    public void setDeliveryType(String deliveryType) { this.deliveryType = deliveryType; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public void setStatus(String status) { this.status = status; this.updatedAt = System.currentTimeMillis(); }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}