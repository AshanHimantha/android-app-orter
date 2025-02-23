package lk.jiat.orterclothing.adpters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

import lk.jiat.orterclothing.R;
import lk.jiat.orterclothing.model.Category;

public class FilterCategoryAdapter extends RecyclerView.Adapter<FilterCategoryAdapter.ViewHolder> {

    private List<Category> categoryList;
    private OnCategoryClickListener listener; // Listener for chip clicks

    public FilterCategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.categoryChip.setText(category.getName()); // Use the category name

        // Set click listener for each chip
        holder.categoryChip.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category); // Pass the selected category
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        Chip categoryChip;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryChip = itemView.findViewById(R.id.chipAll30); // Assuming this is the correct ID for the Chip
        }
    }

    // Interface for handling category clicks
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
}