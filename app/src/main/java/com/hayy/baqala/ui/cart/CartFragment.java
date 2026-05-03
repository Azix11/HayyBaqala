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
import com.hayy.baqala.database.entities.CartItem;
import com.hayy.baqala.databinding.FragmentCartBinding;
import com.hayy.baqala.utils.FirestoreRepository;
import com.hayy.baqala.utils.SessionManager;
import java.util.List;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private SessionManager session;

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
        session = SessionManager.getInstance(requireContext());
        loadCart();
        binding.btnCheckout.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), CheckoutActivity.class)));
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCart();
    }

    private void loadCart() {
        String userId = session.getFirestoreUserId();
        if (userId.isEmpty()) {
            showEmpty();
            return;
        }

        FirestoreRepository.getInstance().getCart(userId, new FirestoreRepository.Callback<List<CartItem>>() {
            @Override
            public void onSuccess(List<CartItem> items) {
                if (binding == null) return;
                if (items != null && !items.isEmpty()) {
                    double total = 0;
                    for (CartItem item : items) total += item.getTotalPrice();

                    binding.layoutEmpty.setVisibility(View.GONE);
                    binding.layoutCart.setVisibility(View.VISIBLE);

                    CartAdapter adapter = new CartAdapter(requireContext(), items, () -> loadCart());
                    binding.rvCart.setLayoutManager(new LinearLayoutManager(requireContext()));
                    binding.rvCart.setAdapter(adapter);

                    binding.tvTotal.setText(String.format("%.2f ر.س", total));
                    binding.tvItemCount.setText(items.size() + " منتج");
                } else {
                    showEmpty();
                }
            }
            @Override
            public void onError(String message) {
                if (binding != null) showEmpty();
            }
        });
    }

    private void showEmpty() {
        if (binding == null) return;
        binding.layoutEmpty.setVisibility(View.VISIBLE);
        binding.layoutCart.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
