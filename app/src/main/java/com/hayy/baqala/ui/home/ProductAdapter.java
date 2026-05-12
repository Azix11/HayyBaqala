package com.hayy.baqala.ui.home;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.hayy.baqala.R;
import com.hayy.baqala.database.entities.CartItem;
import com.hayy.baqala.database.entities.Product;
import com.hayy.baqala.utils.FirestoreRepository;
import com.hayy.baqala.utils.SessionManager;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> products;
    private final int storeId;
    private final SessionManager session;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ProductAdapter(Context context, List<Product> products, int storeId) {
        this.context = context;
        this.products = products;
        this.storeId = storeId;
        this.session = SessionManager.getInstance(context);
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(product.getPrice() + " ر.س");

        if (product.getUnit() != null && !product.getUnit().isEmpty()) {
            holder.tvUnit.setText(product.getUnit());
            holder.tvUnit.setVisibility(View.VISIBLE);
        } else {
            holder.tvUnit.setVisibility(View.GONE);
        }

        holder.btnAddToCart.setOnClickListener(v -> addToCart(product));
    }

    private void addToCart(Product product) {
        String firestoreUserId = session.getFirestoreUserId();
        if (firestoreUserId.isEmpty()) {
            Toast.makeText(context, "يرجى تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        FirestoreRepository.getInstance().getCart(firestoreUserId, new FirestoreRepository.Callback<List<CartItem>>() {
            @Override
            public void onSuccess(List<CartItem> cartItems) {
                CartItem existing = null;
                if (cartItems != null) {
                    for (CartItem c : cartItems) {
                        if (c.getProductId() == product.getId()) {
                            existing = c;
                            break;
                        }
                    }
                }

                if (existing != null) {
                    int newQty = existing.getQuantity() + 1;
                    FirestoreRepository.getInstance().updateCartItemQuantity(
                            firestoreUserId, product.getId(), newQty,
                            new FirestoreRepository.Callback<Void>() {
                                @Override public void onSuccess(Void v) {
                                    mainHandler.post(() ->
                                        Toast.makeText(context, "تمت الإضافة ✓", Toast.LENGTH_SHORT).show());
                                }
                                @Override public void onError(String e) {}
                            });
                } else {
                    CartItem newItem = new CartItem(
                            session.getUserId(), product.getId(), storeId,
                            product.getName(), product.getPrice());
                    newItem.firestoreUserId = firestoreUserId;
                    FirestoreRepository.getInstance().addCartItem(firestoreUserId, newItem,
                            new FirestoreRepository.Callback<Void>() {
                                @Override public void onSuccess(Void v) {
                                    mainHandler.post(() ->
                                        Toast.makeText(context, "أضيف للسلة ✓", Toast.LENGTH_SHORT).show());
                                }
                                @Override public void onError(String e) {}
                            });
                }
            }

            @Override
            public void onError(String e) {}
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice, tvUnit;
        MaterialButton btnAddToCart;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvUnit = itemView.findViewById(R.id.tvUnit);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }
}
