package com.hayy.baqala.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.entities.Store;
import com.hayy.baqala.databinding.ActivitySearchBinding;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = AppDatabase.getInstance(this);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etSearch.requestFocus();
    }

    private void search(String query) {
        if (query.isEmpty()) {
            binding.rvResults.setAdapter(null);
            return;
        }

        List<Store> stores = db.storeDao().searchStores(query);
        if (stores != null && !stores.isEmpty()) {
            StoreAdapter adapter = new StoreAdapter(this, stores);
            binding.rvResults.setLayoutManager(new LinearLayoutManager(this));
            binding.rvResults.setAdapter(adapter);
        } else {
            binding.rvResults.setAdapter(null);
        }
    }
}