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
import com.hayy.baqala.database.AppDatabase;
import com.hayy.baqala.database.entities.CartItem;
import com.hayy.baqala.database.entities.Product;
import com.hayy.baqala.utils.SessionManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> products;
    private int storeId;
    private AppDatabase db;
    private SessionManager session;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ProductAdapter(Context context, List<Product> products, int storeId) {
        this.context = context;
        this.products = products;
        this.storeId = storeId;
        this.db = AppDatabase.getInstance(context);
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

        // تم تعديل الزر ليرسل المنتج مباشرة بدون ملاحظات فردية
        holder.btnAddToCart.setOnClickListener(v -> {
            addToCart(product);
        });
    }

    private void addToCart(Product product) {
        int userId = session.getUserId();
        if (userId == -1) {
            Toast.makeText(context, "يرجى تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        executor.execute(() -> {
            CartItem existingItem = db.cartDao().getCartItemByProduct(userId, product.getId());

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + 1);
                db.cartDao().updateCartItem(existingItem);
                mainHandler.post(() -> Toast.makeText(context, "تمت الإضافة ✓", Toast.LENGTH_SHORT).show());
            } else {
                CartItem cartItem = new CartItem(
                        userId,
                        product.getId(),
                        storeId,
                        product.getName(),
                        product.getPrice()
                );
                // حذفنا سطر setNotes هنا ليكون الوصف عاماً للطلب كاملاً
                db.cartDao().insertCartItem(cartItem);
                mainHandler.post(() -> Toast.makeText(context, "أضيف للسلة ✓", Toast.LENGTH_SHORT).show());
            }
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
            // تم حذف سطر تعريف etNotes لعدم الحاجة له بعد الآن
        }
    }
}
