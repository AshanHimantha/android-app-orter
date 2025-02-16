package lk.jiat.orterclothing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private List<Store> storeList;
    private int selectedPosition = -1;

    public StoreAdapter(List<Store> storeList) {
        this.storeList = storeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Store store = storeList.get(position);
        holder.storeName.setText(store.getStoreName());
        holder.addressLine1.setText(store.getAddress1());
        holder.addressLine2.setText(store.getAddress2() + ", " + store.getZipCode());

        if (store.getStoreImage() != null && !store.getStoreImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(store.getStoreImage())
                    .placeholder(R.drawable.kandy)
                    .into(holder.storeImage);
        }

        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.blackborder);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.light_border);
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public Store getSelectedStore() {
        if (selectedPosition != -1 && selectedPosition < storeList.size()) {
            return storeList.get(selectedPosition);
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView storeImage;
        TextView storeName;
        TextView addressLine1;
        TextView addressLine2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storeImage = itemView.findViewById(R.id.proImage);
            storeName = itemView.findViewById(R.id.textView59);
            addressLine1 = itemView.findViewById(R.id.textView60);
            addressLine2 = itemView.findViewById(R.id.textView61);
        }
    }
}