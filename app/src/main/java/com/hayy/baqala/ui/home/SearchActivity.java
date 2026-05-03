package com.hayy.baqala.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.hayy.baqala.database.entities.Store;
import com.hayy.baqala.databinding.ActivitySearchBinding;
import com.hayy.baqala.utils.FirestoreRepository;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.etSearch.requestFocus();
    }

    private void search(String query) {
        if (query.isEmpty()) {
            binding.rvResults.setAdapter(null);
            return;
        }

        FirestoreRepository.getInstance().searchStores(query, new FirestoreRepository.Callback<List<Store>>() {
            @Override
            public void onSuccess(List<Store> stores) {
                if (stores != null && !stores.isEmpty()) {
                    StoreAdapter adapter = new StoreAdapter(SearchActivity.this, stores, 0, 0);
                    binding.rvResults.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
                    binding.rvResults.setAdapter(adapter);
                } else {
                    binding.rvResults.setAdapter(null);
                }
            }
            @Override
            public void onError(String message) {
                binding.rvResults.setAdapter(null);
            }
        });
    }
}
