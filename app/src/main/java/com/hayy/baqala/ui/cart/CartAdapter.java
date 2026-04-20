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
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.entities.CartItem;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> items;
    private AppDatabase db;
    private OnCartChangedListener listener;

    public interface OnCartChangedListener {
        void onCartChanged();
    }

    public CartAdapter(Context context, List<CartItem> items, OnCartChangedListener listener) {
        this.context = context;
        this.items = items;
        this.db = AppDatabase.getInstance(context);
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
            item.setQuantity(item.getQuantity() + 1);
            db.cartDao().updateCartItem(item);
            notifyItemChanged(position);
            listener.onCartChanged();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                db.cartDao().updateCartItem(item);
                notifyItemChanged(position);
            } else {
                db.cartDao().deleteCartItem(item);
                items.remove(position);
                notifyItemRemoved(position);
            }
            listener.onCartChanged();
        });

        holder.btnDelete.setOnClickListener(v -> {
            db.cartDao().deleteCartItem(item);
            items.remove(position);
            notifyItemRemoved(position);
            listener.onCartChanged();
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