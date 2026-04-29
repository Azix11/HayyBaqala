package com.hayy.baqala.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.entities.CartItem;
import com.hayy.baqala.databinding.FragmentCartBinding;
import com.hayy.baqala.utils.SessionManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
        private AppDatabase db;
        private SessionManager session;
        private CartAdapter adapter;
        private final ExecutorService executor = Executors.newSingleThreadExecutor();
        private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                                              @Nullable ViewGroup container,
                                                              @Nullable Bundle savedInstanceState) {
                    binding = FragmentCartBinding.inflate(inflater, container, false);
                    return binding.getRoot();
        }

    @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
                    super.onViewCreated(view, savedInstanceState);

            db = AppDatabase.getInstance(requireContext());
                    session = SessionManager.getInstance(requireContext());

            loadCart();

            binding.btnCheckout.setOnClickListener(v -> {
                            Intent intent = new Intent(requireContext(), CheckoutActivity.class);
                            startActivity(intent);
            });
        }

    @Override
        public void onResume() {
                    super.onResume();
                    loadCart();
        }

    private void loadCart() {
                int userId = session.getUserId();
                executor.execute(() -> {
                                List<CartItem> items = db.cartDao().getCartItems(userId);
                                double total = db.cartDao().getCartTotal(userId);
                                mainHandler.post(() -> {
                                                    if (binding == null) return;
                                                    if (items != null && !items.isEmpty()) {
                                                                            binding.layoutEmpty.setVisibility(View.GONE);
                                                                            binding.layoutCart.setVisibility(View.VISIBLE);

                                                        adapter = new CartAdapter(requireContext(), items, () -> loadCart());
                                                                            binding.rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
                                                                            binding.rvCart.setAdapter(adapter);

                                                        binding.tvTotal.setText(String.format("%.2f ر.س", total));
                                                                            binding.tvItemCount.setText(items.size() + " منتج");
                                                    } else {
                                                                            binding.layoutEmpty.setVisibility(View.VISIBLE);
                                                                            binding.layoutCart.setVisibility(View.GONE);
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
