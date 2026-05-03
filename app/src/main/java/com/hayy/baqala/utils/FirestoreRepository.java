package com.hayy.baqala.utils;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.hayy.baqala.database.entities.CartItem;
import com.hayy.baqala.database.entities.Order;
import com.hayy.baqala.database.entities.Product;
import com.hayy.baqala.database.entities.Store;
import com.hayy.baqala.database.entities.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreRepository {

    private static FirestoreRepository instance;
    private final FirebaseFirestore db;

    private static final String STORES = "stores";
    private static final String PRODUCTS = "products";
    private static final String USERS = "users";
    private static final String ORDERS = "orders";
    private static final String CART = "cart";
    private static final String ITEMS = "items";

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    private FirestoreRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirestoreRepository getInstance() {
        if (instance == null) instance = new FirestoreRepository();
        return instance;
    }

    // ── Stores ───────────────────────────────────────────────────────────────

    public void getStores(Callback<List<Store>> callback) {
        db.collection(STORES)
                .orderBy("numId")
                .get()
                .addOnSuccessListener(snap -> {
                    List<Store> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) list.add(docToStore(doc));
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getStore(String docId, Callback<Store> callback) {
        db.collection(STORES).document(docId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) callback.onSuccess(docToStore(doc));
                    else callback.onError("المتجر غير موجود");
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getStoreByNumId(int numId, Callback<Store> callback) {
        db.collection(STORES).whereEqualTo("numId", numId).limit(1).get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty())
                        callback.onSuccess(docToStore(snap.getDocuments().get(0)));
                    else callback.onError("المتجر غير موجود");
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void searchStores(String query, Callback<List<Store>> callback) {
        db.collection(STORES).get()
                .addOnSuccessListener(snap -> {
                    List<Store> list = new ArrayList<>();
                    String q = query.toLowerCase();
                    for (QueryDocumentSnapshot doc : snap) {
                        Store s = docToStore(doc);
                        if ((s.getName() != null && s.getName().contains(q)) ||
                            (s.getAddress() != null && s.getAddress().contains(q))) {
                            list.add(s);
                        }
                    }
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ── Products ─────────────────────────────────────────────────────────────

    public void getProducts(int storeNumId, Callback<List<Product>> callback) {
        db.collection(PRODUCTS)
                .whereEqualTo("storeId", storeNumId)
                .whereEqualTo("isAvailable", true)
                .get()
                .addOnSuccessListener(snap -> {
                    List<Product> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) list.add(docToProduct(doc));
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ── Users ─────────────────────────────────────────────────────────────────

    public void getUserByCredentials(String username, String password, Callback<User> callback) {
        db.collection(USERS)
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .limit(1)
                .get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) {
                        DocumentSnapshot doc = snap.getDocuments().get(0);
                        User user = docToUser(doc);
                        callback.onSuccess(user);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getUserByUsername(String username, Callback<User> callback) {
        db.collection(USERS).whereEqualTo("username", username).limit(1).get()
                .addOnSuccessListener(snap -> {
                    if (!snap.isEmpty()) callback.onSuccess(docToUser(snap.getDocuments().get(0)));
                    else callback.onSuccess(null);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void createUser(String name, String username, String password, Callback<String> callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("username", username);
        data.put("password", password);
        data.put("email", "");
        data.put("phone", "");
        data.put("createdAt", System.currentTimeMillis());
        data.put("isAdmin", false);

        db.collection(USERS).add(data)
                .addOnSuccessListener(ref -> callback.onSuccess(ref.getId()))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getUserById(String fsId, Callback<User> callback) {
        db.collection(USERS).document(fsId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) callback.onSuccess(docToUser(doc));
                    else callback.onError("المستخدم غير موجود");
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateUserName(String fsId, String name, Callback<Void> callback) {
        db.collection(USERS).document(fsId)
                .update("name", name)
                .addOnSuccessListener(v -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ── Cart ──────────────────────────────────────────────────────────────────

    public void getCart(String userId, Callback<List<CartItem>> callback) {
        db.collection(CART).document(userId).collection(ITEMS).get()
                .addOnSuccessListener(snap -> {
                    List<CartItem> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) list.add(docToCartItem(doc, userId));
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void addCartItem(String userId, CartItem item, Callback<Void> callback) {
        String docId = String.valueOf(item.getProductId());
        Map<String, Object> data = new HashMap<>();
        data.put("productId", item.getProductId());
        data.put("productName", item.getProductName());
        data.put("price", item.getPrice());
        data.put("quantity", item.getQuantity());
        data.put("storeId", item.getStoreId());

        db.collection(CART).document(userId).collection(ITEMS).document(docId)
                .set(data)
                .addOnSuccessListener(v -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateCartItemQuantity(String userId, int productId, int quantity, Callback<Void> callback) {
        if (quantity <= 0) {
            removeCartItem(userId, productId, callback);
            return;
        }
        db.collection(CART).document(userId).collection(ITEMS).document(String.valueOf(productId))
                .update("quantity", quantity)
                .addOnSuccessListener(v -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void removeCartItem(String userId, int productId, Callback<Void> callback) {
        db.collection(CART).document(userId).collection(ITEMS).document(String.valueOf(productId))
                .delete()
                .addOnSuccessListener(v -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void clearCart(String userId, Callback<Void> callback) {
        db.collection(CART).document(userId).collection(ITEMS).get()
                .addOnSuccessListener(snap -> {
                    if (snap.isEmpty()) { callback.onSuccess(null); return; }
                    final int[] remaining = {snap.size()};
                    for (DocumentSnapshot doc : snap.getDocuments()) {
                        doc.getReference().delete().addOnCompleteListener(t -> {
                            remaining[0]--;
                            if (remaining[0] == 0) callback.onSuccess(null);
                        });
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ── Orders ────────────────────────────────────────────────────────────────

    public void createOrder(Order order, String firestoreUserId, Callback<String> callback) {
        Map<String, Object> data = new HashMap<>();
        data.put("firestoreUserId", firestoreUserId);
        data.put("userId", order.getUserId());
        data.put("storeId", order.getStoreId());
        data.put("storeName", order.getStoreName());
        data.put("grandTotal", order.getGrandTotal());
        data.put("deliveryType", order.getDeliveryType());
        data.put("deliveryAddress", order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "");
        data.put("status", Constants.STATUS_PENDING);
        data.put("notes", order.getNotes() != null ? order.getNotes() : "");
        data.put("createdAt", System.currentTimeMillis());

        db.collection(ORDERS).add(data)
                .addOnSuccessListener(ref -> callback.onSuccess(ref.getId()))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getOrdersByUser(String firestoreUserId, Callback<List<Order>> callback) {
        db.collection(ORDERS)
                .whereEqualTo("firestoreUserId", firestoreUserId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    List<Order> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) list.add(docToOrder(doc));
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getAllOrders(Callback<List<Order>> callback) {
        db.collection(ORDERS)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    List<Order> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) list.add(docToOrder(doc));
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getOrdersByStatus(String status, Callback<List<Order>> callback) {
        db.collection(ORDERS)
                .whereEqualTo("status", status)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snap -> {
                    List<Order> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : snap) list.add(docToOrder(doc));
                    callback.onSuccess(list);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getOrderById(String orderId, Callback<Order> callback) {
        db.collection(ORDERS).document(orderId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) callback.onSuccess(docToOrder(doc));
                    else callback.onError("الطلب غير موجود");
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateOrderStatus(String orderId, String status, Callback<Void> callback) {
        db.collection(ORDERS).document(orderId)
                .update("status", status, "updatedAt", System.currentTimeMillis())
                .addOnSuccessListener(v -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ── Seeding check ─────────────────────────────────────────────────────────

    public void isSeeded(Callback<Boolean> callback) {
        db.collection(STORES).limit(1).get()
                .addOnSuccessListener(snap -> callback.onSuccess(!snap.isEmpty()))
                .addOnFailureListener(e -> callback.onSuccess(false));
    }

    public void addStore(Map<String, Object> data, Callback<String> callback) {
        db.collection(STORES).add(data)
                .addOnSuccessListener(ref -> callback.onSuccess(ref.getId()))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void addProduct(Map<String, Object> data, Callback<String> callback) {
        db.collection(PRODUCTS).add(data)
                .addOnSuccessListener(ref -> callback.onSuccess(ref.getId()))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ── Converters ────────────────────────────────────────────────────────────

    private Store docToStore(DocumentSnapshot doc) {
        Store s = new Store();
        s.id = safeInt(doc, "numId");
        s.name = doc.getString("name");
        s.description = doc.getString("description");
        s.address = doc.getString("address");
        s.phone = doc.getString("phone");
        s.latitude = safeDouble(doc, "latitude");
        s.longitude = safeDouble(doc, "longitude");
        s.rating = safeFloat(doc, "rating");
        Boolean open = doc.getBoolean("isOpen");
        s.isOpen = open != null && open;
        Boolean delivery = doc.getBoolean("deliveryAvailable");
        s.deliveryAvailable = delivery != null && delivery;
        s.deliveryFee = safeDouble(doc, "deliveryFee");
        s.minOrder = safeDouble(doc, "minOrder");
        s.openingTime = doc.getString("openingTime");
        s.closingTime = doc.getString("closingTime");
        s.firestoreId = doc.getId();
        return s;
    }

    private Product docToProduct(DocumentSnapshot doc) {
        Product p = new Product();
        p.id = safeInt(doc, "numId");
        p.storeId = safeInt(doc, "storeId");
        p.name = doc.getString("name");
        p.price = safeDouble(doc, "price");
        p.category = doc.getString("category");
        p.unit = doc.getString("unit");
        p.stock = safeInt(doc, "stock");
        Boolean avail = doc.getBoolean("isAvailable");
        p.isAvailable = avail != null && avail;
        p.description = doc.getString("description");
        p.firestoreId = doc.getId();
        return p;
    }

    private User docToUser(DocumentSnapshot doc) {
        User u = new User();
        u.id = safeInt(doc, "numId");
        u.name = doc.getString("name");
        u.username = doc.getString("username");
        u.password = doc.getString("password");
        u.phone = doc.getString("phone");
        u.email = doc.getString("email");
        u.firestoreId = doc.getId();
        return u;
    }

    private CartItem docToCartItem(DocumentSnapshot doc, String userId) {
        CartItem c = new CartItem();
        c.setProductId(safeInt(doc, "productId"));
        c.setProductName(doc.getString("productName"));
        c.setPrice(safeDouble(doc, "price"));
        c.setQuantity(safeInt(doc, "quantity"));
        c.setStoreId(safeInt(doc, "storeId"));
        c.setUserId(userId.hashCode() & 0x7FFFFFFF);
        c.firestoreUserId = userId;
        return c;
    }

    private Order docToOrder(DocumentSnapshot doc) {
        Order o = new Order();
        o.id = safeInt(doc, "userId");
        o.userId = safeInt(doc, "userId");
        o.storeId = safeInt(doc, "storeId");
        o.storeName = doc.getString("storeName");
        o.grandTotal = safeDouble(doc, "grandTotal");
        o.deliveryType = doc.getString("deliveryType");
        o.deliveryAddress = doc.getString("deliveryAddress");
        o.status = doc.getString("status");
        o.notes = doc.getString("notes");
        o.createdAt = safeLong(doc, "createdAt");
        o.firestoreId = doc.getId();
        return o;
    }

    private int safeInt(DocumentSnapshot doc, String key) {
        Long v = doc.getLong(key);
        return v != null ? v.intValue() : 0;
    }

    private double safeDouble(DocumentSnapshot doc, String key) {
        Double v = doc.getDouble(key);
        return v != null ? v : 0.0;
    }

    private float safeFloat(DocumentSnapshot doc, String key) {
        Double v = doc.getDouble(key);
        return v != null ? v.floatValue() : 0f;
    }

    private long safeLong(DocumentSnapshot doc, String key) {
        Long v = doc.getLong(key);
        return v != null ? v : 0L;
    }
}
