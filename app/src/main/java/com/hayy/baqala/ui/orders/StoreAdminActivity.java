package com.hayy.baqala.ui.orders;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.hayy.baqala.R;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.entities.Order;
import com.hayy.baqala.databinding.ActivityStoreAdminBinding;
import com.hayy.baqala.utils.Constants;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoreAdminActivity extends AppCompatActivity {

    private ActivityStoreAdminBinding binding;
        private AppDatabase db;
        private AdminOrderAdapter adapter;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final String[] STATUS_ORDER = {
                Constants.STATUS_PENDING,
                Constants.STATUS_CONFIRMED,
                Constants.STATUS_PREPARING,
                Constants.STATUS_DELIVERING,
                Constants.STATUS_DELIVERED
    };

    private static final String[] TAB_LABELS = {
                "الكل", "قيد الانتظار", "مؤكد", "يتحضر", "في الطريق", "مُسلَّم"
    };

    private String currentFilter = null; // null = all

    @Override
        protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    binding = ActivityStoreAdminBinding.inflate(getLayoutInflater());
                    setContentView(binding.getRoot());

            db = AppDatabase.getInstance(this);

            binding.btnBack.setOnClickListener(v -> finish());

            setupTabs();
                    setupRecyclerView();
                    loadOrders();
        }

    private void setupTabs() {
                for (String label : TAB_LABELS) {
                                binding.tabLayout.addTab(binding.tabLayout.newTab().setText(label));
                }

            binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                                int pos = tab.getPosition();
                                                currentFilter = pos == 0 ? null : STATUS_ORDER[pos - 1];
                                                loadOrders();
                            }
                            @Override public void onTabUnselected(TabLayout.Tab tab) {}
                            @Override public void onTabReselected(TabLayout.Tab tab) {}
            });
    }

    private void setupRecyclerView() {
                adapter = new AdminOrderAdapter(this, new ArrayList<>(), order -> {
                                String nextStatus = getNextStatus(order.getStatus());
                                if (nextStatus != null) {
                                                    executor.execute(() -> {
                                                                            db.orderDao().updateOrderStatus(order.getId(), nextStatus, System.currentTimeMillis());
                                                                            loadOrders();
                                                    });
                                }
                });
                binding.rvOrders.setLayoutManager(new LinearLayoutManager(this));
                binding.rvOrders.setAdapter(adapter);
    }

    private void loadOrders() {
                String filter = currentFilter;
                executor.execute(() -> {
                                List<Order> orders = new ArrayList<>();
                                if (filter == null) {
                                                    for (int storeId = 1; storeId <= 10; storeId++) {
                                                                            orders.addAll(db.orderDao().getOrdersByStore(storeId));
                                                    }
                                } else {
                                                    for (int storeId = 1; storeId <= 10; storeId++) {
                                                                            List<Order> storeOrders = db.orderDao().getOrdersByStore(storeId);
                                                                            for (Order o : storeOrders) {
                                                                                                        if (o.getStatus().equals(filter)) orders.add(o);
                                                                                }
                                                    }
                                }

                                             long pendingCount = 0;
                                for (Order o : orders) {
                                                    if (Constants.STATUS_PENDING.equals(o.getStatus())) pendingCount++;
                                }
                                final long finalPendingCount = pendingCount;

                                             mainHandler.post(() -> {
                                                                 binding.tvPendingCount.setText(finalPendingCount + " طلبات بانتظارك");
                                                                 if (orders.isEmpty()) {
                                                                                         binding.tvEmpty.setVisibility(View.VISIBLE);
                                                                                         binding.rvOrders.setVisibility(View.GONE);
                                                                 } else {
                                                                                         binding.tvEmpty.setVisibility(View.GONE);
                                                                                         binding.rvOrders.setVisibility(View.VISIBLE);
                                                                                         adapter.updateOrders(orders);
                                                                 }
                                             });
                });
    }

    private String getNextStatus(String current) {
                switch (current) {
                    case Constants.STATUS_PENDING: return Constants.STATUS_CONFIRMED;
                    case Constants.STATUS_CONFIRMED: return Constants.STATUS_PREPARING;
                    case Constants.STATUS_PREPARING: return Constants.STATUS_DELIVERING;
                    case Constants.STATUS_DELIVERING: return Constants.STATUS_DELIVERED;
                    default: return null;
                }
    }

    private String getNextStatusLabel(String current) {
                switch (current) {
                    case Constants.STATUS_PENDING: return "تأكيد الطلب ✅";
                    case Constants.STATUS_CONFIRMED: return "بدء التحضير 👨‍🍳";
                    case Constants.STATUS_PREPARING: return "أرسال للتوصيل 🚚";
                    case Constants.STATUS_DELIVERING: return "تم التسليم ✅";
                    default: return null;
                }
    }

    private String getStatusLabel(String status) {
                switch (status) {
                    case Constants.STATUS_PENDING: return "⏳ قيد الانتظار";
                    case Constants.STATUS_CONFIRMED: return "✅ مؤكد";
                    case Constants.STATUS_PREPARING: return "👨‍🍳 يتحضر";
                    case Constants.STATUS_DELIVERING: return "🚚 في الطريق";
                    case Constants.STATUS_DELIVERED: return "✅ مُسلَّم";
                    case Constants.STATUS_CANCELLED: return "❌ ملغي";
                    default: return status;
                }
    }

    @Override
        protected void onDestroy() {
                    super.onDestroy();
                    executor.shutdown();
        }

    // ── Adapter ──────────────────────────────────────────────────────────────

    interface OnNextStatusListener {
                void onNext(Order order);
    }

    class AdminOrderAdapter extends RecyclerView.Adapter<AdminOrderAdapter.ViewHolder> {

            private final Context context;
                private List<Order> orders;
                private final OnNextStatusListener listener;
                private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());

            AdminOrderAdapter(Context context, List<Order> orders, OnNextStatusListener listener) {
                            this.context = context;
                            this.orders = orders;
                            this.listener = listener;
            }

            void updateOrders(List<Order> newOrders) {
                            this.orders = newOrders;
                            notifyDataSetChanged();
            }

            @NonNull
                @Override
                public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View v = LayoutInflater.from(context).inflate(R.layout.item_admin_order, parent, false);
                                return new ViewHolder(v);
                }

            @Override
                public void onBindViewHolder(@NonNull ViewHolder h, int position) {
                                Order order = orders.get(position);

                    h.tvOrderNumber.setText("طلب #" + order.getId() + " • " + order.getStoreName());
                                h.tvStatus.setText(getStatusLabel(order.getStatus()));
                                h.tvTotal.setText(String.format("%.2f ر.س", order.getGrandTotal()));
                                h.tvDate.setText(sdf.format(new Date(order.getCreatedAt())));

                    if (Constants.DELIVERY_HOME.equals(order.getDeliveryType())) {
                                        String addr = order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "";
                                        h.tvDeliveryType.setText("🚚 توصيل — " + addr);
                    } else {
                                        h.tvDeliveryType.setText("🏪 استلام من المحل");
                    }

                    if (order.getNotes() != null && !order.getNotes().isEmpty()) {
                                        h.tvNotes.setVisibility(View.VISIBLE);
                                        h.tvNotes.setText("📝 " + order.getNotes());
                    } else {
                                        h.tvNotes.setVisibility(View.GONE);
                    }

                    String nextLabel = getNextStatusLabel(order.getStatus());
                                if (nextLabel != null) {
                                                    h.layoutActions.setVisibility(View.VISIBLE);
                                                    h.btnNextStatus.setText(nextLabel);
                                                    h.btnNextStatus.setOnClickListener(v -> listener.onNext(order));
                                } else {
                                                    h.layoutActions.setVisibility(View.GONE);
                                }
                }

            @Override
                public int getItemCount() { return orders.size(); }

            class ViewHolder extends RecyclerView.ViewHolder {
                            TextView tvOrderNumber, tvStatus, tvTotal, tvDate, tvDeliveryType, tvNotes;
                            MaterialButton btnNextStatus;
                            View layoutActions;

                    ViewHolder(@NonNull View itemView) {
                                        super(itemView);
                                        tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
                                        tvStatus = itemView.findViewById(R.id.tvStatus);
                                        tvTotal = itemView.findViewById(R.id.tvTotal);
                                        tvDate = itemView.findViewById(R.id.tvDate);
                                        tvDeliveryType = itemView.findViewById(R.id.tvDeliveryType);
                                        tvNotes = itemView.findViewById(R.id.tvNotes);
                                        btnNextStatus = itemView.findViewById(R.id.btnNextStatus);
                                        layoutActions = itemView.findViewById(R.id.layoutActions);
                    }
            }
    }
}
