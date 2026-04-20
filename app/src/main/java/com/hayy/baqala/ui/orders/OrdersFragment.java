package com.hayy.baqala.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.entities.Order;
import com.hayy.baqala.databinding.FragmentOrdersBinding;
import com.hayy.baqala.utils.SessionManager;
import java.util.List;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private AppDatabase db;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = AppDatabase.getInstance(requireContext());
        session = SessionManager.getInstance(requireContext());

        loadOrders();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        int userId = session.getUserId();
        List<Order> orders = db.orderDao().getOrdersByUser(userId);

        if (orders != null && !orders.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.rvOrders.setVisibility(View.VISIBLE);
            OrdersAdapter adapter = new OrdersAdapter(requireContext(), orders);
            binding.rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.rvOrders.setAdapter(adapter);
        } else {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvOrders.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}