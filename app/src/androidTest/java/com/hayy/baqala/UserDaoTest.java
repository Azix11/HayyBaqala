package com.hayy.baqala;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.dao.UserDao;
import com.hayy.baqala.database.entities.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.List;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class UserDaoTest {

    private AppDatabase db;
    private UserDao userDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        userDao = db.userDao();
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void insertUser_andGetById() {
        User user = new User("Ahmed", "0501234567", "ahmed@test.com");
        long id = userDao.insertUser(user);
        User retrieved = userDao.getUserById((int) id);
        assertNotNull(retrieved);
        assertEquals("Ahmed", retrieved.getName());
        assertEquals("0501234567", retrieved.getPhone());
        assertEquals("ahmed@test.com", retrieved.getEmail());
    }

    @Test
    public void getUserByPhone_returnsCorrectUser() {
        userDao.insertUser(new User("Sara", "0509876543", "sara@test.com"));
        User retrieved = userDao.getUserByPhone("0509876543");
        assertNotNull(retrieved);
        assertEquals("Sara", retrieved.getName());
    }

    @Test
    public void getUserByEmail_returnsCorrectUser() {
        userDao.insertUser(new User("Mohammed", "0551234567", "mohammed@test.com"));
        User retrieved = userDao.getUserByEmail("mohammed@test.com");
        assertNotNull(retrieved);
        assertEquals("Mohammed", retrieved.getName());
    }

    @Test
    public void getUserByPhone_nonExistent_returnsNull() {
        User retrieved = userDao.getUserByPhone("0000000000");
        assertNull(retrieved);
    }

    @Test
    public void getUserByEmail_nonExistent_returnsNull() {
        User retrieved = userDao.getUserByEmail("nobody@test.com");
        assertNull(retrieved);
    }

    @Test
    public void getAllUsers_returnsAll() {
        userDao.insertUser(new User("User1", "0501111111", "user1@test.com"));
        userDao.insertUser(new User("User2", "0502222222", "user2@test.com"));
        List<User> users = userDao.getAllUsers();
        assertEquals(2, users.size());
    }

    @Test
    public void getAllUsers_emptyDb_returnsEmptyList() {
        List<User> users = userDao.getAllUsers();
        assertTrue(users.isEmpty());
    }

    @Test
    public void updateUser_changesData() {
        long id = userDao.insertUser(new User("Ahmed", "0501234567", "ahmed@test.com"));
        User retrieved = userDao.getUserById((int) id);
        retrieved.setName("Ahmed Updated");
        userDao.updateUser(retrieved);
        User updated = userDao.getUserById((int) id);
        assertEquals("Ahmed Updated", updated.getName());
    }

    @Test
    public void deleteUser_removesFromDb() {
        long id = userDao.insertUser(new User("Ahmed", "0501234567", "ahmed@test.com"));
        User retrieved = userDao.getUserById((int) id);
        userDao.deleteUser(retrieved);
        assertNull(userDao.getUserById((int) id));
    }

    @Test
    public void deleteAllUsers_clearsTable() {
        userDao.insertUser(new User("User1", "0501111111", "user1@test.com"));
        userDao.insertUser(new User("User2", "0502222222", "user2@test.com"));
        userDao.deleteAllUsers();
        assertTrue(userDao.getAllUsers().isEmpty());
    }

    @Test
    public void insertUser_autoGeneratesId() {
        long id1 = userDao.insertUser(new User("User1", "0501111111", "user1@test.com"));
        long id2 = userDao.insertUser(new User("User2", "0502222222", "user2@test.com"));
        assertNotEquals(id1, id2);
    }
}
