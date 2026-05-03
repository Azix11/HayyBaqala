package com.hayy.baqala.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.hayy.baqala.database.entities.CartItem;
import com.hayy.baqala.database.entities.Order;
import com.hayy.baqala.databinding.ActivityCheckoutBinding;
import com.hayy.baqala.ui.orders.OrderTrackingActivity;
import com.hayy.baqala.utils.Constants;
import com.hayy.baqala.utils.FirestoreRepository;
import com.hayy.baqala.utils.SessionManager;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private SessionManager session;
    private double cartTotal = 0;
    private double deliveryFee = 0;
    private String deliveryType = Constants.DELIVERY_PICKUP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        String userId = session.getFirestoreUserId();
        FirestoreRepository.getInstance().getCart(userId, new FirestoreRepository.Callback<List<CartItem>>() {
            @Override
            public void onSuccess(List<CartItem> items) {
                double total = 0;
                if (items != null) {
                    for (CartItem item : items) total += item.getTotalPrice();
                    binding.tvItemCount.setText(items.size() + " منتج");
                }
                cartTotal = total;
                binding.tvCartTotal.setText(String.format("%.2f ر.س", cartTotal));
                binding.tvDeliveryFee.setText("0.00 ر.س");
                binding.tvGrandTotal.setText(String.format("%.2f ر.س", cartTotal));
                binding.tvPaymentMethod.setText("الدفع عند الاستلام (كاش) 💵");
            }
            @Override
            public void onError(String message) {}
        });
    }

    private void updateTotal() {
        binding.tvDeliveryFee.setText(String.format("%.2f ر.س", deliveryFee));
        binding.tvGrandTotal.setText(String.format("%.2f ر.س", cartTotal + deliveryFee));
    }

    private void placeOrder() {
        if (deliveryType.equals(Constants.DELIVERY_HOME)) {
            String address = binding.etAddress.getText() != null
                    ? binding.etAddress.getText().toString().trim() : "";
            if (address.isEmpty()) {
                binding.etAddress.setError("أدخل عنوان التوصيل");
                return;
            }
        }

        String deliveryAddress = deliveryType.equals(Constants.DELIVERY_HOME)
                && binding.etAddress.getText() != null
                ? binding.etAddress.getText().toString().trim() : "";

        String firestoreUserId = session.getFirestoreUserId();
        binding.btnPlaceOrder.setEnabled(false);

        FirestoreRepository.getInstance().getCart(firestoreUserId, new FirestoreRepository.Callback<List<CartItem>>() {
            @Override
            public void onSuccess(List<CartItem> items) {
                if (items == null || items.isEmpty()) {
                    binding.btnPlaceOrder.setEnabled(true);
                    Toast.makeText(CheckoutActivity.this, "السلة فارغة", Toast.LENGTH_SHORT).show();
                    return;
                }

                int storeId = items.get(0).getStoreId();
                Order order = new Order(session.getUserId(), storeId, "البقالة", cartTotal + deliveryFee, deliveryType);
                if (!deliveryAddress.isEmpty()) order.setDeliveryAddress(deliveryAddress);

                FirestoreRepository.getInstance().createOrder(order, firestoreUserId, new FirestoreRepository.Callback<String>() {
                    @Override
                    public void onSuccess(String orderId) {
                        FirestoreRepository.getInstance().clearCart(firestoreUserId, new FirestoreRepository.Callback<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                Intent intent = new Intent(CheckoutActivity.this, OrderTrackingActivity.class);
                                intent.putExtra("firestore_order_id", orderId);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                            @Override
                            public void onError(String msg) { navigateToTracking(orderId); }
                        });
                    }
                    @Override
                    public void onError(String msg) {
                        binding.btnPlaceOrder.setEnabled(true);
                        Toast.makeText(CheckoutActivity.this, "خطأ في تأكيد الطلب: " + msg, Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onError(String msg) {
                binding.btnPlaceOrder.setEnabled(true);
            }
        });
    }

    private void navigateToTracking(String orderId) {
        Intent intent = new Intent(this, OrderTrackingActivity.class);
        intent.putExtra("firestore_order_id", orderId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
