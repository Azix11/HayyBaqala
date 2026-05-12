package com.hayy.baqala.ui.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hayy.baqala.R;
import com.hayy.baqala.database.entities.CartItem;
import com.hayy.baqala.utils.FirestoreRepository;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    private final List<CartItem> items;
    private final OnCartChangedListener listener;

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    public CartAdapter(Context context, List<CartItem> items, OnCartChangedListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);

        holder.tvName.setText(item.getProductName());
        holder.tvPrice.setText(String.format("%.2f ر.س", item.getPrice()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvTotal.setText(String.format("%.2f ر.س", item.getTotalPrice()));

        holder.btnIncrease.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_ID) return;
            int newQty = item.getQuantity() + 1;
            item.setQuantity(newQty);
            notifyItemChanged(adapterPos);
            FirestoreRepository.getInstance().updateCartItemQuantity(
                    item.firestoreUserId, item.getProductId(), newQty,
                    new FirestoreRepository.Callback<Void>() {
                        @Override public void onSuccess(Void v) { listener.onCartChanged(); }
                        @Override public void onError(String e) {}
                    });
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_ID) return;
            if (item.getQuantity() > 1) {
                int newQty = item.getQuantity() - 1;
                item.setQuantity(newQty);
                notifyItemChanged(adapterPos);
                FirestoreRepository.getInstance().updateCartItemQuantity(
                        item.firestoreUserId, item.getProductId(), newQty,
                        new FirestoreRepository.Callback<Void>() {
                            @Override public void onSuccess(Void v) { listener.onCartChanged(); }
                            @Override public void onError(String e) {}
                        });
            } else {
                items.remove(adapterPos);
                notifyItemRemoved(adapterPos);
                FirestoreRepository.getInstance().removeCartItem(
                        item.firestoreUserId, item.getProductId(),
                        new FirestoreRepository.Callback<Void>() {
                            @Override public void onSuccess(Void v) { listener.onCartChanged(); }
                            @Override public void onError(String e) {}
                        });
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_ID) return;
            items.remove(adapterPos);
            notifyItemRemoved(adapterPos);
            FirestoreRepository.getInstance().removeCartItem(
                    item.firestoreUserId, item.getProductId(),
                    new FirestoreRepository.Callback<Void>() {
                        @Override public void onSuccess(Void v) { listener.onCartChanged(); }
                        @Override public void onError(String e) {}
                    });
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, tvTotal;
        ImageButton btnIncrease, btnDecrease, btnDelete;

        CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
