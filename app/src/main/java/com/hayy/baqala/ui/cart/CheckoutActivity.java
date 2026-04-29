package com.hayy.baqala.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
        private AppDatabase db;
        private SessionManager session;
        private double cartTotal = 0;
        private double deliveryFee = 0;
        private String deliveryType = Constants.DELIVERY_PICKUP;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final Handler mainHandler = new Handler(Looper.getMainLooper());

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
                executor.execute(() -> {
                                List<CartItem> items = db.cartDao().getCartItems(userId);
                                double total = db.cartDao().getCartTotal(userId);
                                mainHandler.post(() -> {
                                                    cartTotal = total;
                                                    if (items != null) {
                                                                            binding.tvItemCount.setText(items.size() + " منتج");
                                                    }
                                                    binding.tvCartTotal.setText(String.format("%.2f ر.س", cartTotal));
                                                    binding.tvDeliveryFee.setText("0.00 ر.س");
                                                    binding.tvGrandTotal.setText(String.format("%.2f ر.س", cartTotal));
                                                    binding.tvPaymentMethod.setText("الدفع عند الاستلام (كاش) 💵");
                                });
                });
    }

    private void updateTotal() {
                double grandTotal = cartTotal + deliveryFee;
                binding.tvDeliveryFee.setText(String.format("%.2f ر.س", deliveryFee));
                binding.tvGrandTotal.setText(String.format("%.2f ر.س", grandTotal));
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

            int userId = session.getUserId();
                binding.btnPlaceOrder.setEnabled(false);

            executor.execute(() -> {
                            List<CartItem> items = db.cartDao().getCartItems(userId);

                                         if (items == null || items.isEmpty()) {
                                                             mainHandler.post(() -> {
                                                                                     binding.btnPlaceOrder.setEnabled(true);
                                                                                     Toast.makeText(this, "السلة فارغة", Toast.LENGTH_SHORT).show();
                                                             });
                                                             return;
                                         }

                                         int storeId = items.get(0).getStoreId();
                            String storeName = "البقالة";

                                         Order order = new Order(userId, storeId, storeName, cartTotal + deliveryFee, deliveryType);
                            if (!deliveryAddress.isEmpty()) {
                                                order.setDeliveryAddress(deliveryAddress);
                            }

                                         long orderId = db.orderDao().insertOrder(order);
                            db.cartDao().clearCart(userId);

                                         mainHandler.post(() -> {
                                                             Intent intent = new Intent(this, OrderTrackingActivity.class);
                                                             intent.putExtra(Constants.KEY_ORDER_ID, (int) orderId);
                                                             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                             startActivity(intent);
                                                             finish();
                                         });
            });
    }

    @Override
        protected void onDestroy() {
                    super.onDestroy();
                    executor.shutdown();
        }
}
