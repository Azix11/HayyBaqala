package com.hayy.baqala.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.entities.Store;
import com.hayy.baqala.databinding.FragmentHomeBinding;
import com.hayy.baqala.utils.SessionManager;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private AppDatabase db;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());
        session = SessionManager.getInstance(requireContext());

        // تحية المستخدم
        binding.tvWelcome.setText("أهلاً، " + session.getUserName() + " 👋");

        // تحميل البقالات
        loadStores();

        // البحث
        binding.etSearch.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SearchActivity.class);
            startActivity(intent);
        });
    }

    private void loadStores() {
        List<Store> stores = db.storeDao().getAllStores();
        if (stores != null && !stores.isEmpty()) {
            StoreAdapter adapter = new StoreAdapter(requireContext(), stores);
            binding.rvStores.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rvStores.setAdapter(adapter);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}