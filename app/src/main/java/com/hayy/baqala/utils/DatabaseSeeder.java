package com.hayy.baqala.utils;

import android.content.Context;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.entities.Product;
import com.hayy.baqala.database.entities.Store;
import java.util.List;

public class DatabaseSeeder {

    public static void seedIfEmpty(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);

        List<Store> stores = db.storeDao().getAllStores();
        if (!stores.isEmpty()) return;

        // إضافة بقالات تجريبية
        Store store1 = new Store();
        store1.name = "بقالة الأمل";
        store1.description = "بقالة متنوعة بأسعار مناسبة";
        store1.address = "حي النزهة، الرياض";
        store1.phone = "0501234567";
        store1.latitude = 24.7136;
        store1.longitude = 46.6753;
        store1.rating = 4.5f;
        store1.isOpen = true;
        store1.deliveryAvailable = true;
        store1.deliveryFee = 10.0;
        store1.minOrder = 20.0;
        store1.openingTime = "07:00";
        store1.closingTime = "24:00";
        long storeId1 = db.storeDao().insertStore(store1);

        Store store2 = new Store();
        store2.name = "سوبرماركت النور";
        store2.description = "أكبر تشكيلة من المنتجات الطازجة";
        store2.address = "حي العليا، الرياض";
        store2.phone = "0507654321";
        store2.latitude = 24.7200;
        store2.longitude = 46.6900;
        store2.rating = 4.8f;
        store2.isOpen = true;
        store2.deliveryAvailable = true;
        store2.deliveryFee = 10.0;
        store2.minOrder = 30.0;
        store2.openingTime = "06:00";
        store2.closingTime = "24:00";
        long storeId2 = db.storeDao().insertStore(store2);

        Store store3 = new Store();
        store3.name = "بقالة الحي";
        store3.description = "كل احتياجاتك اليومية";
        store3.address = "حي الملقا، الرياض";
        store3.phone = "0509876543";
        store3.latitude = 24.7500;
        store3.longitude = 46.6400;
        store3.rating = 4.2f;
        store3.isOpen = true;
        store3.deliveryAvailable = false;
        store3.deliveryFee = 0.0;
        store3.minOrder = 0.0;
        store3.openingTime = "08:00";
        store3.closingTime = "23:00";
        long storeId3 = db.storeDao().insertStore(store3);

        // إضافة منتجات للبقالة الأولى
        addProducts(db, (int) storeId1);
        addProducts(db, (int) storeId2);
        addProducts(db, (int) storeId3);
    }

    private static void addProducts(AppDatabase db, int storeId) {

        // مشروبات
        Product p1 = new Product(storeId, "ماء معدني 1.5 لتر", 1.5, "مشروبات");
        p1.unit = "قارورة"; p1.stock = 100; p1.isAvailable = true;
        db.productDao().insertProduct(p1);

        Product p2 = new Product(storeId, "عصير برتقال طازج", 5.0, "مشروبات");
        p2.unit = "عبوة"; p2.stock = 50; p2.isAvailable = true;
        db.productDao().insertProduct(p2);

        Product p3 = new Product(storeId, "كولا 330 مل", 2.0, "مشروبات");
        p3.unit = "علبة"; p3.stock = 200; p3.isAvailable = true;
        db.productDao().insertProduct(p3);

        // خبز ومعجنات
        Product p4 = new Product(storeId, "خبز تميس", 1.0, "خبز ومعجنات");
        p4.unit = "رغيف"; p4.stock = 80; p4.isAvailable = true;
        db.productDao().insertProduct(p4);

        Product p5 = new Product(storeId, "خبز توست", 4.5, "خبز ومعجنات");
        p5.unit = "كيس"; p5.stock = 60; p5.isAvailable = true;
        db.productDao().insertProduct(p5);

        // ألبان وأجبان
        Product p6 = new Product(storeId, "حليب طازج 1 لتر", 4.0, "ألبان وأجبان");
        p6.unit = "لتر"; p6.stock = 70; p6.isAvailable = true;
        db.productDao().insertProduct(p6);

        Product p7 = new Product(storeId, "لبن رايب", 3.5, "ألبان وأجبان");
        p7.unit = "كوب"; p7.stock = 90; p7.isAvailable = true;
        db.productDao().insertProduct(p7);

        Product p8 = new Product(storeId, "جبن أبيض", 8.0, "ألبان وأجبان");
        p8.unit = "250 جرام"; p8.stock = 40; p8.isAvailable = true;
        db.productDao().insertProduct(p8);

        // وجبات خفيفة
        Product p9 = new Product(storeId, "شيبس نكهة جبن", 3.0, "وجبات خفيفة");
        p9.unit = "كيس"; p9.stock = 150; p9.isAvailable = true;
        db.productDao().insertProduct(p9);

        Product p10 = new Product(storeId, "شوكولاتة كيت كات", 4.0, "وجبات خفيفة");
        p10.unit = "قطعة"; p10.stock = 100; p10.isAvailable = true;
        db.productDao().insertProduct(p10);

        // معلبات
        Product p11 = new Product(storeId, "تونة معلبة", 6.5, "معلبات");
        p11.unit = "علبة"; p11.stock = 80; p11.isAvailable = true;
        db.productDao().insertProduct(p11);

        Product p12 = new Product(storeId, "فاصوليا معلبة", 4.0, "معلبات");
        p12.unit = "علبة"; p12.stock = 60; p12.isAvailable = true;
        db.productDao().insertProduct(p12);

        // منظفات
        Product p13 = new Product(storeId, "صابون يدين", 5.0, "منظفات");
        p13.unit = "قطعة"; p13.stock = 100; p13.isAvailable = true;
        db.productDao().insertProduct(p13);

        Product p14 = new Product(storeId, "منظف أطباق", 9.0, "منظفات");
        p14.unit = "عبوة"; p14.stock = 70; p14.isAvailable = true;
        db.productDao().insertProduct(p14);
    }
}