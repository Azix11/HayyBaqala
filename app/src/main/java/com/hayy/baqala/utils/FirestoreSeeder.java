package com.hayy.baqala.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class FirestoreSeeder {

    public interface SeedCallback {
        void onComplete();
    }

    public static void seedIfEmpty(SeedCallback callback) {
        FirestoreRepository repo = FirestoreRepository.getInstance();
        repo.isSeeded(new FirestoreRepository.Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean seeded) {
                if (Boolean.TRUE.equals(seeded)) {
                    callback.onComplete();
                } else {
                    seedStores(callback);
                }
            }
            @Override
            public void onError(String message) {
                seedStores(callback);
            }
        });
    }

    private static void seedStores(SeedCallback callback) {
        FirestoreRepository repo = FirestoreRepository.getInstance();

        Object[][] stores = {
            // { numId, name, description, address, phone, lat, lon, rating, isOpen, deliveryAvail, deliveryFee, minOrder, open, close }
            {1, "بقالة الأمل",        "بقالة متنوعة بأسعار مناسبة",          "حي النزهة، الرياض",      "0501234567", 24.7136, 46.6753, 4.5f, true,  true,  10.0, 20.0, "07:00", "24:00"},
            {2, "سوبرماركت النور",     "أكبر تشكيلة من المنتجات الطازجة",      "حي العليا، الرياض",       "0507654321", 24.6877, 46.7219, 4.8f, true,  true,  10.0, 30.0, "06:00", "24:00"},
            {3, "بقالة الحي",          "كل احتياجاتك اليومية",                "حي الملقا، الرياض",       "0509876543", 24.7741, 46.6384, 4.2f, true,  false,  0.0,  0.0, "08:00", "23:00"},
            {4, "سوبرماركت الروضة",    "منتجات طازجة يومياً",                 "حي الروضة، الرياض",       "0552345678", 24.7204, 46.7100, 4.6f, true,  true,  10.0, 25.0, "07:00", "24:00"},
            {5, "بقالة العزيزية",      "جودة عالية وأسعار منافسة",            "حي العزيزية، الرياض",     "0553456789", 24.6550, 46.7750, 4.3f, true,  true,  12.0, 20.0, "08:00", "23:00"},
            {6, "سوبرماركت الخالدية",  "تشكيلة واسعة من البضائع",             "حي الخالدية، الرياض",     "0554567890", 24.7450, 46.6550, 4.7f, true,  true,   8.0, 15.0, "06:00", "24:00"},
            {7, "بقالة الورود",        "الطازج دائماً",                       "حي الورود، الرياض",       "0555678901", 24.7900, 46.6900, 4.1f, true,  true,  10.0, 20.0, "07:00", "23:00"},
            {8, "ميني ماركت الصحافة",  "قريب منك في كل وقت",                 "حي الصحافة، الرياض",      "0556789012", 24.8050, 46.6250, 4.4f, true,  true,   9.0, 18.0, "07:00", "24:00"},
            {9, "سوبرماركت النخيل",    "كل شيء تحت سقف واحد",                "حي النخيل، الرياض",       "0557890123", 24.8200, 46.7050, 4.5f, true,  true,  10.0, 22.0, "06:00", "24:00"},
            {10,"بقالة الربيع",        "طازج ومختار بعناية",                  "حي الربيع، الرياض",       "0558901234", 24.8350, 46.6700, 4.3f, true,  false,  0.0,  0.0, "08:00", "22:00"},
            {11,"سوبرماركت الشفا",     "خدمة سريعة وتوصيل فوري",             "حي الشفا، الرياض",        "0559012345", 24.6200, 46.7100, 4.6f, true,  true,  11.0, 25.0, "07:00", "24:00"},
            {12,"بقالة بدر",           "أسعار لا تقاوم",                      "حي بدر، الرياض",          "0551123456", 24.5950, 46.7300, 4.2f, true,  true,  10.0, 20.0, "08:00", "23:00"},
            {13,"سوبرماركت الدرعية",   "التراث والحداثة معاً",                "حي الدرعية، الرياض",      "0551234567", 24.7300, 46.5700, 4.7f, true,  true,  12.0, 30.0, "07:00", "24:00"},
        };

        AtomicInteger remaining = new AtomicInteger(stores.length);
        AtomicInteger storeCounter = new AtomicInteger(0);

        for (Object[] s : stores) {
            Map<String, Object> data = new HashMap<>();
            data.put("numId",            s[0]);
            data.put("name",             s[1]);
            data.put("description",      s[2]);
            data.put("address",          s[3]);
            data.put("phone",            s[4]);
            data.put("latitude",         s[5]);
            data.put("longitude",        s[6]);
            data.put("rating",           ((Float)s[7]).doubleValue());
            data.put("isOpen",           s[8]);
            data.put("deliveryAvailable",s[9]);
            data.put("deliveryFee",      s[10]);
            data.put("minOrder",         s[11]);
            data.put("openingTime",      s[12]);
            data.put("closingTime",      s[13]);

            final int storeNumId = (int) s[0];

            FirestoreRepository.getInstance().addStore(data, new FirestoreRepository.Callback<String>() {
                @Override
                public void onSuccess(String docId) {
                    seedProductsForStore(storeNumId);
                    if (remaining.decrementAndGet() == 0) callback.onComplete();
                }
                @Override
                public void onError(String message) {
                    if (remaining.decrementAndGet() == 0) callback.onComplete();
                }
            });
        }
    }

    private static void seedProductsForStore(int storeNumId) {
        Object[][] products = {
            {"ماء معدني 1.5 لتر",    1.5,  "مشروبات",       "قارورة",  100},
            {"عصير برتقال طازج",      5.0,  "مشروبات",       "عبوة",     50},
            {"كولا 330 مل",           2.0,  "مشروبات",       "علبة",    200},
            {"خبز تميس",              1.0,  "خبز ومعجنات",   "رغيف",     80},
            {"خبز توست",              4.5,  "خبز ومعجنات",   "كيس",      60},
            {"حليب طازج 1 لتر",       4.0,  "ألبان وأجبان",  "لتر",      70},
            {"لبن رايب",              3.5,  "ألبان وأجبان",  "كوب",      90},
            {"جبن أبيض",              8.0,  "ألبان وأجبان",  "250 جرام", 40},
            {"شيبس نكهة جبن",         3.0,  "وجبات خفيفة",   "كيس",     150},
            {"شوكولاتة كيت كات",      4.0,  "وجبات خفيفة",   "قطعة",    100},
            {"تونة معلبة",            6.5,  "معلبات",        "علبة",     80},
            {"فاصوليا معلبة",         4.0,  "معلبات",        "علبة",     60},
            {"صابون يدين",            5.0,  "منظفات",        "قطعة",    100},
            {"منظف أطباق",            9.0,  "منظفات",        "عبوة",     70},
        };

        AtomicInteger numId = new AtomicInteger(storeNumId * 100);
        for (Object[] p : products) {
            Map<String, Object> data = new HashMap<>();
            data.put("numId",       numId.getAndIncrement());
            data.put("storeId",     storeNumId);
            data.put("name",        p[0]);
            data.put("price",       p[1]);
            data.put("category",    p[2]);
            data.put("unit",        p[3]);
            data.put("stock",       p[4]);
            data.put("isAvailable", true);
            data.put("description", "");
            FirestoreRepository.getInstance().addProduct(data, new FirestoreRepository.Callback<String>() {
                @Override public void onSuccess(String r) {}
                @Override public void onError(String e) {}
            });
        }
    }
}
