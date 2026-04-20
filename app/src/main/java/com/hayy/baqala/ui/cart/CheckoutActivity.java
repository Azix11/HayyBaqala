package com.hayy.baqala.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.entities.CartItem;
import com.hayy.baqala.database.entities.Order;
import com.hayy.baqala.databinding.ActivityCheckoutBinding;
import com.hayy.baqala.ui.orders.OrderTrackingActivity;
import com.hayy.baqala.utils.Constants;
import com.hayy.baqala.utils.SessionManager;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private AppDatabase db;
    private SessionManager session;
    private double cartTotal = 0;
    private double deliveryFee = 0;
    private String deliveryType = Constants.DELIVERY_PICKUP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);
        session = SessionManager.getInstance(this);

        binding.btnBack.setOnClickListener(v -> finish());

        loadCartSummary();

        binding.rgDeliveryType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == binding.rbDelivery.getId()) {
                deliveryType = Constants.DELIVERY_HOME;
                deliveryFee = Constants.DELIVERY_FEE;
                binding.layoutAddress.setVisibility(View.VISIBLE);
            } else {
                deliveryType = Constants.DELIVERY_PICKUP;
                deliveryFee = 0;
                binding.layoutAddress.setVisibility(View.GONE);
            }
            updateTotal();
        });

        binding.btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void loadCartSummary() {
        int userId = session.getUserId();
        List<CartItem> items = db.cartDao().getCartItems(userId);
        cartTotal = db.cartDao().getCartTotal(userId);

        binding.tvItemCount.setText(items.size() + " منتج");
        binding.tvCartTotal.setText(String.format("%.2f ر.س", cartTotal));
        binding.tvDeliveryFee.setText("0.00 ر.س");
        binding.tvGrandTotal.setText(String.format("%.2f ر.س", cartTotal));
        binding.tvPaymentMethod.setText("الدفع عند الاستلام (كاش) 💵");
    }

    private void updateTotal() {
        double grandTotal = cartTotal + deliveryFee;
        binding.tvDeliveryFee.setText(String.format("%.2f ر.س", deliveryFee));
        binding.tvGrandTotal.setText(String.format("%.2f ر.س", grandTotal));
    }

    private void placeOrder() {
        int userId = session.getUserId();
        List<CartItem> items = db.cartDao().getCartItems(userId);

        if (items == null || items.isEmpty()) {
            Toast.makeText(this, "السلة فارغة", Toast.LENGTH_SHORT).show();
            return;
        }

        if (deliveryType.equals(Constants.DELIVERY_HOME)) {
            String address = binding.etAddress.getText().toString().trim();
            if (address.isEmpty()) {
                binding.etAddress.setError("أدخل عنوان التوصيل");
                return;
            }
        }

        int storeId = items.get(0).getStoreId();
        String storeName = "البقالة";

        Order order = new Order(userId, storeId, storeName, cartTotal, deliveryType);

        if (deliveryType.equals(Constants.DELIVERY_HOME)) {
            order.setDeliveryAddress(binding.etAddress.getText().toString().trim());
        }

        long orderId = db.orderDao().insertOrder(order);
        db.cartDao().clearCart(userId);

        Intent intent = new Intent(this, OrderTrackingActivity.class);
        intent.putExtra(Constants.KEY_ORDER_ID, (int) orderId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}