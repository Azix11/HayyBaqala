package com.hayy.baqala.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.hayy.baqala.database.dao.CartDao;
import com.hayy.baqala.database.dao.OrderDao;
import com.hayy.baqala.database.dao.ProductDao;
import com.hayy.baqala.database.dao.StoreDao;
import com.hayy.baqala.database.dao.UserDao;
import com.hayy.baqala.database.entities.CartItem;
import com.hayy.baqala.database.entities.Order;
import com.hayy.baqala.database.entities.Product;
import com.hayy.baqala.database.entities.Store;
import com.hayy.baqala.database.entities.User;

@Database(
    entities = {
        User.class,
        Store.class,
        Product.class,
        CartItem.class,
        Order.class
    },
    version = 2,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "hayy_baqala_db";
    private static volatile AppDatabase instance;

    public abstract UserDao userDao();
    public abstract StoreDao storeDao();
    public abstract ProductDao productDao();
    public abstract CartDao cartDao();
    public abstract OrderDao orderDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }
}
