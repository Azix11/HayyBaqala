package com.hayy.baqala.ui.home;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.hayy.baqala.R;
import com.hayy.baqala.database.entities.Product;
import com.hayy.baqala.database.entities.Store;
import com.hayy.baqala.databinding.ActivityStoreBinding;
import com.hayy.baqala.utils.Constants;
import com.hayy.baqala.utils.FirestoreRepository;
import java.util.List;

public class StoreActivity extends AppCompatActivity {

    private ActivityStoreBinding binding;
    private int storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storeId = getIntent().getIntExtra(Constants.KEY_STORE_ID, -1);
        if (storeId == -1) { finish(); return; }

        binding.btnBack.setOnClickListener(v -> finish());
        loadStoreAndProducts();
    }

    private void loadStoreAndProducts() {
        FirestoreRepository.getInstance().getStoreByNumId(storeId, new FirestoreRepository.Callback<Store>() {
            @Override
            public void onSuccess(Store store) {
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

                loadProducts();
            }
            @Override
            public void onError(String message) { finish(); }
        });
    }

    private void loadProducts() {
        FirestoreRepository.getInstance().getProducts(storeId, new FirestoreRepository.Callback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> products) {
                if (products != null && !products.isEmpty()) {
                    binding.rvProducts.setVisibility(View.VISIBLE);
                    binding.tvEmpty.setVisibility(View.GONE);
                    ProductAdapter adapter = new ProductAdapter(StoreActivity.this, products, storeId);
                    binding.rvProducts.setLayoutManager(new GridLayoutManager(StoreActivity.this, 2));
                    binding.rvProducts.setAdapter(adapter);
                } else {
                    binding.rvProducts.setVisibility(View.GONE);
                    binding.tvEmpty.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onError(String message) {
                binding.rvProducts.setVisibility(View.GONE);
                binding.tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }
}
