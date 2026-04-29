package com.hayy.baqala.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.hayy.baqala.R;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.entities.Product;
import com.hayy.baqala.database.entities.Store;
import com.hayy.baqala.databinding.ActivityStoreBinding;
import com.hayy.baqala.utils.Constants;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StoreActivity extends AppCompatActivity {

    private ActivityStoreBinding binding;
        private AppDatabase db;
        private int storeId;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
        protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    binding = ActivityStoreBinding.inflate(getLayoutInflater());
                    setContentView(binding.getRoot());

            db = AppDatabase.getInstance(this);
                    storeId = getIntent().getIntExtra(Constants.KEY_STORE_ID, -1);

            if (storeId == -1) {
                            finish();
                            return;
            }

            loadStoreAndProducts();
                    binding.btnBack.setOnClickListener(v -> finish());
        }

    private void loadStoreAndProducts() {
                executor.execute(() -> {
                                Store store = db.storeDao().getStoreById(storeId);
                                List<Product> products = db.productDao().getAvailableProducts(storeId);
                                mainHandler.post(() -> {
                                                    if (store != null) {
                                                                            binding.tvStoreName.setText(store.getName());
                                                                            binding.tvStoreAddress.setText(store.getAddress());
                                                                            binding.tvRating.setText(String.valueOf(store.getRating()));

                                                        if (store.isOpen()) {
                                                                                    binding.tvStatus.setText("مفتوح الآن");
                                                                                    binding.tvStatus.setTextColor(getColor(R.color.success));
                                                        } else {
                                                                                    binding.tvStatus.setText("مغلق");
                                                                                    binding.tvStatus.setTextColor(getColor(R.color.error));
                                                        }

                                                        if (store.isDeliveryAvailable()) {
                                                                                    binding.tvDeliveryInfo.setText("توصيل " + store.getDeliveryFee() + " ر.س • حد أدنى " + store.getMinOrder() + " ر.س");
                                                        } else {
                                                                                    binding.tvDeliveryInfo.setText("استلام من المحل فقط");
                                                        }
                                                    }

                                                                 if (products != null && !products.isEmpty()) {
                                                                                         binding.rvProducts.setVisibility(View.VISIBLE);
                                                                                         binding.tvEmpty.setVisibility(View.GONE);
                                                                                         ProductAdapter adapter = new ProductAdapter(this, products, storeId);
                                                                                         binding.rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
                                                                                         binding.rvProducts.setAdapter(adapter);
                                                                 } else {
                                                                                         binding.rvProducts.setVisibility(View.GONE);
                                                                                         binding.tvEmpty.setVisibility(View.VISIBLE);
                                                                 }
                                });
                });
    }

    @Override
        protected void onDestroy() {
                    super.onDestroy();
                    executor.shutdown();
        }
}
