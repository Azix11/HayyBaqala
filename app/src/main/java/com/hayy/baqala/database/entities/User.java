package com.hayy.baqala.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

@Entity(tableName = "users")
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "profile_image")
    public String profileImage;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    public User() {}

    public User(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.createdAt = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getProfileImage() { return profileImage; }
    public String getAddress() { return address; }
    public long getCreatedAt() { return createdAt; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public void setAddress(String address) { this.address = address; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}