package com.hayy.baqala.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.entities.Store;
import com.hayy.baqala.databinding.FragmentHomeBinding;
import com.hayy.baqala.utils.SessionManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
        private AppDatabase db;
        private SessionManager session;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final Handler mainHandler = new Handler(Looper.getMainLooper());

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
            String userName = session.getUserName();
                    if (userName != null && !userName.isEmpty()) {
                                    binding.tvWelcome.setText("أهلاً، " + userName + " 👋");
                    } else {
                                    binding.tvWelcome.setText("أهلاً 👋");
                    }

            // تحميل البقالات
            loadStores();

            // البحث
            binding.etSearch.setOnClickListener(v -> {
                            Intent intent = new Intent(requireContext(), SearchActivity.class);
                            startActivity(intent);
            });
        }

    private void loadStores() {
                executor.execute(() -> {
                                List<Store> stores = db.storeDao().getAllStores();
                                mainHandler.post(() -> {
                                                    if (binding == null) return;
                                                    if (stores != null && !stores.isEmpty()) {
                                                                            StoreAdapter adapter = new StoreAdapter(requireContext(), stores);
                                                                            binding.rvStores.setLayoutManager(new LinearLayoutManager(requireContext()));
                                                                            binding.rvStores.setAdapter(adapter);
                                                    } else {
                                                                            Toast.makeText(requireContext(), "لا توجد بقالات متاحة", Toast.LENGTH_SHORT).show();
                                                    }
                                });
                });
    }

    @Override
        public void onDestroyView() {
                    super.onDestroyView();
                    binding = null;
        }

    @Override
        public void onDestroy() {
                    super.onDestroy();
                    executor.shutdown();
        }
}
