package com.hayy.baqala;

import com.hayy.baqala.database.entities.User;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserEntityTest {

    @Test
    public void constructor_setsNamePhoneEmail() {
        User user = new User("Ahmed", "0501234567", "ahmed@test.com");
        assertEquals("Ahmed", user.getName());
        assertEquals("0501234567", user.getPhone());
        assertEquals("ahmed@test.com", user.getEmail());
    }

    @Test
    public void newUser_createdAtIsSet() {
        long before = System.currentTimeMillis();
        User user = new User("Ahmed", "0501234567", "ahmed@test.com");
        long after = System.currentTimeMillis();
        assertTrue(user.getCreatedAt() >= before && user.getCreatedAt() <= after);
    }

    @Test
    public void setName_updatesName() {
        User user = new User("Ahmed", "0501234567", "ahmed@test.com");
        user.setName("Mohammed");
        assertEquals("Mohammed", user.getName());
    }

    @Test
    public void setPhone_updatesPhone() {
        User user = new User("Ahmed", "0501234567", "ahmed@test.com");
        user.setPhone("0559999999");
        assertEquals("0559999999", user.getPhone());
    }

    @Test
    public void setEmail_updatesEmail() {
        User user = new User("Ahmed", "0501234567", "ahmed@test.com");
        user.setEmail("newemail@test.com");
        assertEquals("newemail@test.com", user.getEmail());
    }

    @Test
    public void setAddress_storesValue() {
        User user = new User("Ahmed", "0501234567", "ahmed@test.com");
        user.setAddress("Riyadh, Al Malaz");
        assertEquals("Riyadh, Al Malaz", user.getAddress());
    }

    @Test
    public void setProfileImage_storesValue() {
        User user = new User("Ahmed", "0501234567", "ahmed@test.com");
        user.setProfileImage("profile.jpg");
        assertEquals("profile.jpg", user.getProfileImage());
    }

    @Test
    public void newUser_profileImageIsNull() {
        User user = new User("Ahmed", "0501234567", "ahmed@test.com");
        assertNull(user.getProfileImage());
    }

    @Test
    public void newUser_addressIsNull() {
        User user = new User("Ahmed", "0501234567", "ahmed@test.com");
        assertNull(user.getAddress());
    }

    @Test
    public void defaultConstructor_worksForRoom() {
        User user = new User();
        assertNotNull(user);
    }
}
