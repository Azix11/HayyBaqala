package com.hayy.baqala.ui.orders;

import com.hayy.baqala.R;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.hayy.baqala.database.entities.Order;
import com.hayy.baqala.databinding.ActivityOrderTrackingBinding;
import com.hayy.baqala.utils.Constants;
import com.hayy.baqala.utils.FirestoreRepository;

public class OrderTrackingActivity extends AppCompatActivity {

    private ActivityOrderTrackingBinding binding;
    private String firestoreOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderTrackingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestoreOrderId = getIntent().getStringExtra("firestore_order_id");
        if (firestoreOrderId == null || firestoreOrderId.isEmpty()) {
            finish();
            return;
        }

        binding.btnBack.setOnClickListener(v -> finish());
        loadOrder();
    }

    private void loadOrder() {
        FirestoreRepository.getInstance().getOrderById(firestoreOrderId, new FirestoreRepository.Callback<Order>() {
            @Override
            public void onSuccess(Order order) {
                String shortId = firestoreOrderId.length() >= 6
                        ? firestoreOrderId.substring(0, 6).toUpperCase() : firestoreOrderId;
                binding.tvOrderNumber.setText("طلب #" + shortId);
                binding.tvStoreName.setText(order.getStoreName());
                binding.tvTotal.setText(String.format("%.2f ر.س", order.getGrandTotal()));

                if (Constants.DELIVERY_HOME.equals(order.getDeliveryType())) {
                    binding.tvDeliveryType.setText("🚚 توصيل للمنزل");
                    binding.layoutAddress.setVisibility(View.VISIBLE);
                    binding.tvAddress.setText(order.getDeliveryAddress());
                } else {
                    binding.tvDeliveryType.setText("🏪 استلام من المحل");
                    binding.layoutAddress.setVisibility(View.GONE);
                }

                updateStatusUI(order.getStatus());
            }

            @Override
            public void onError(String message) {
                finish();
            }
        });
    }

    private void updateStatusUI(String status) {
        setStepInactive(binding.step1, binding.tvStep1);
        setStepInactive(binding.step2, binding.tvStep2);
        setStepInactive(binding.step3, binding.tvStep3);
        setStepInactive(binding.step4, binding.tvStep4);

        if (status == null) return;
        switch (status) {
            case Constants.STATUS_PENDING:
                setStepActive(binding.step1, binding.tvStep1);
                binding.tvCurrentStatus.setText("⏳ في انتظار تأكيد الطلب");
                binding.tvCurrentStatus.setTextColor(getColor(R.color.status_pending));
                break;
            case Constants.STATUS_CONFIRMED:
                setStepActive(binding.step1, binding.tvStep1);
                setStepActive(binding.step2, binding.tvStep2);
                binding.tvCurrentStatus.setText("✅ تم تأكيد طلبك");
                binding.tvCurrentStatus.setTextColor(getColor(R.color.status_confirmed));
                break;
            case Constants.STATUS_PREPARING:
                setStepActive(binding.step1, binding.tvStep1);
                setStepActive(binding.step2, binding.tvStep2);
                setStepActive(binding.step3, binding.tvStep3);
                binding.tvCurrentStatus.setText("👨‍🍳 جاري تحضير طلبك");
                binding.tvCurrentStatus.setTextColor(getColor(R.color.status_confirmed));
                break;
            case Constants.STATUS_DELIVERING:
                setStepActive(binding.step1, binding.tvStep1);
                setStepActive(binding.step2, binding.tvStep2);
                setStepActive(binding.step3, binding.tvStep3);
                setStepActive(binding.step4, binding.tvStep4);
                binding.tvCurrentStatus.setText("🚚 طلبك في الطريق إليك");
                binding.tvCurrentStatus.setTextColor(getColor(R.color.status_delivering));
                break;
            case Constants.STATUS_DELIVERED:
                setStepActive(binding.step1, binding.tvStep1);
                setStepActive(binding.step2, binding.tvStep2);
                setStepActive(binding.step3, binding.tvStep3);
                setStepActive(binding.step4, binding.tvStep4);
                binding.tvCurrentStatus.setText("✅ تم استلام طلبك");
                binding.tvCurrentStatus.setTextColor(getColor(R.color.status_delivered));
                break;
        }
    }

    private void setStepActive(View circle, android.widget.TextView label) {
        circle.setBackgroundResource(R.drawable.bg_step_active);
        label.setTextColor(getColor(R.color.primary));
    }

    private void setStepInactive(View circle, android.widget.TextView label) {
        circle.setBackgroundResource(R.drawable.bg_step_inactive);
        label.setTextColor(getColor(R.color.text_hint));
    }
}
