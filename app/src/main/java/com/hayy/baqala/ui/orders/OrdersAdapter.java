package com.hayy.baqala.ui.orders;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hayy.baqala.R;
import com.hayy.baqala.database.entities.Order;
import com.hayy.baqala.utils.Constants;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private final Context context;
    private final List<Order> orders;

    public OrdersAdapter(Context context, List<Order> orders) {
        this.context = context;
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        String shortId = (order.firestoreId != null && order.firestoreId.length() >= 6)
                ? order.firestoreId.substring(0, 6).toUpperCase() : String.valueOf(position + 1);
        holder.tvOrderNumber.setText("طلب #" + shortId);
        holder.tvStoreName.setText(order.getStoreName());
        holder.tvTotal.setText(String.format("%.2f ر.س", order.getGrandTotal()));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(order.getCreatedAt())));

        if (Constants.DELIVERY_HOME.equals(order.getDeliveryType())) {
            holder.tvDeliveryType.setText("🚚 توصيل");
        } else {
            holder.tvDeliveryType.setText("🏪 استلام");
        }

        String status = order.getStatus() != null ? order.getStatus() : "";
        switch (status) {
            case Constants.STATUS_PENDING:
                holder.tvStatus.setText("⏳ قيد الانتظار");
                holder.tvStatus.setTextColor(context.getColor(R.color.status_pending));
                break;
            case Constants.STATUS_CONFIRMED:
                holder.tvStatus.setText("✅ تم التأكيد");
                holder.tvStatus.setTextColor(context.getColor(R.color.status_confirmed));
                break;
            case Constants.STATUS_PREPARING:
                holder.tvStatus.setText("👨‍🍳 جاري التحضير");
                holder.tvStatus.setTextColor(context.getColor(R.color.status_confirmed));
                break;
            case Constants.STATUS_DELIVERING:
                holder.tvStatus.setText("🚚 جاري التوصيل");
                holder.tvStatus.setTextColor(context.getColor(R.color.status_delivering));
                break;
            case Constants.STATUS_DELIVERED:
                holder.tvStatus.setText("✅ تم التوصيل");
                holder.tvStatus.setTextColor(context.getColor(R.color.status_delivered));
                break;
            case Constants.STATUS_CANCELLED:
                holder.tvStatus.setText("❌ ملغي");
                holder.tvStatus.setTextColor(context.getColor(R.color.status_cancelled));
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (order.firestoreId == null || order.firestoreId.isEmpty()) return;
            Intent intent = new Intent(context, OrderTrackingActivity.class);
            intent.putExtra("firestore_order_id", order.firestoreId);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderNumber, tvStoreName, tvTotal, tvDate, tvDeliveryType, tvStatus;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvStoreName = itemView.findViewById(R.id.tvStoreName);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDeliveryType = itemView.findViewById(R.id.tvDeliveryType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
