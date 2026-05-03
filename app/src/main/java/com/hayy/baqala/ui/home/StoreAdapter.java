package com.hayy.baqala.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import com.hayy.baqala.R;
import com.hayy.baqala.database.entities.Store;
import com.hayy.baqala.utils.Constants;
import com.hayy.baqala.utils.LocationHelper;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private Context context;
    private List<Store> stores;
    private double userLat;
    private double userLon;

    public StoreAdapter(Context context, List<Store> stores, double userLat, double userLon) {
        this.context = context;
        this.stores = stores;
        this.userLat = userLat;
        this.userLon = userLon;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_store, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        Store store = stores.get(position);

        holder.tvName.setText(store.getName());
        holder.tvAddress.setText(store.getAddress());
        holder.tvRating.setText(String.valueOf(store.getRating()));

        if (store.isOpen()) {
            holder.chipStatus.setText("مفتوح الآن");
            holder.chipStatus.setChipBackgroundColorResource(R.color.success);
        } else {
            holder.chipStatus.setText("مغلق");
            holder.chipStatus.setChipBackgroundColorResource(R.color.error);
        }

        boolean hasDelivery = store.isDeliveryAvailable();
        if (hasDelivery) {
            holder.tvDelivery.setText("توصيل متاح • " + store.getDeliveryFee() + " ر.س");
            holder.tvDelivery.setVisibility(View.VISIBLE);
        } else {
            holder.tvDelivery.setText("استلام فقط");
        }

        // Distance and delivery time
        if (userLat != 0 && userLon != 0 && store.getLatitude() != 0 && store.getLongitude() != 0) {
            float distanceMeters = LocationHelper.distanceMeters(userLat, userLon, store.getLatitude(), store.getLongitude());
            holder.tvDistance.setText(LocationHelper.formatDistance(distanceMeters));
            holder.tvDistance.setVisibility(View.VISIBLE);

            if (hasDelivery) {
                int minutes = LocationHelper.estimateDeliveryMinutes(distanceMeters);
                holder.tvDeliveryTime.setText("وقت التوصيل: " + minutes + " دقيقة");
                holder.layoutDeliveryTime.setVisibility(View.VISIBLE);
            } else {
                holder.layoutDeliveryTime.setVisibility(View.GONE);
            }
        } else {
            holder.tvDistance.setVisibility(View.GONE);
            holder.layoutDeliveryTime.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, StoreActivity.class);
            intent.putExtra(Constants.KEY_STORE_ID, store.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return stores.size();
    }

    static class StoreViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStore;
        TextView tvName, tvAddress, tvRating, tvDelivery, tvDistance, tvDeliveryTime;
        Chip chipStatus;
        LinearLayout layoutDeliveryTime;

        StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStore = itemView.findViewById(R.id.ivStore);
            tvName = itemView.findViewById(R.id.tvName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDelivery = itemView.findViewById(R.id.tvDelivery);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvDeliveryTime = itemView.findViewById(R.id.tvDeliveryTime);
            chipStatus = itemView.findViewById(R.id.chipStatus);
            layoutDeliveryTime = itemView.findViewById(R.id.layoutDeliveryTime);
        }
    }
}
