package com.hayy.baqala.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.Query;
import com.hayy.baqala.database.entities.User;
import java.util.List;

@Dao
public interface UserDao {

    @Insert
    long insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(int id);

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    User getUserByPhone(String phone);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users")
    List<User> getAllUsers();

    @Query("DELETE FROM users")
    void deleteAllUsers();
}