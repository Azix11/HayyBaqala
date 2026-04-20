package com.hayy.baqala.ui.cart;

import android.content.Intent;
import android.os.Bundle;
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

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private AppDatabase db;
    private SessionManager session;
    private CartAdapter adapter;

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
        List<CartItem> items = db.cartDao().getCartItems(userId);

        if (items != null && !items.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.layoutCart.setVisibility(View.VISIBLE);

            adapter = new CartAdapter(requireContext(), items, () -> loadCart());
            binding.rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rvCart.setAdapter(adapter);

            double total = db.cartDao().getCartTotal(userId);
            binding.tvTotal.setText(String.format("%.2f ر.س", total));
            binding.tvItemCount.setText(items.size() + " منتج");

        } else {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.layoutCart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}