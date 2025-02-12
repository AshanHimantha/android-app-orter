package lk.jiat.orter.ui.cart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.List;
import lk.jiat.orter.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItemList;

    public CartAdapter(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);
        holder.nameTextView.setText(cartItem.getProduct().getName());
        holder.priceTextView.setText(String.valueOf(cartItem.getProduct().getPrice()));
        holder.quantityTextView.setText(String.valueOf(cartItem.getQuantity()));
        holder.sizeTextView.setText(cartItem.getSize());
        holder.categoriesTextView.setText(cartItem.getProduct().getCategoryName());
        Glide.with(holder.itemView.getContext()).load(cartItem.getProduct().getMainImage()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView, quantityTextView, sizeTextView, categoriesTextView;
        RoundedImageView imageView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textView9);
            priceTextView = itemView.findViewById(R.id.textView21);
            quantityTextView = itemView.findViewById(R.id.textView25);
            imageView = itemView.findViewById(R.id.imageView5);
            sizeTextView = itemView.findViewById(R.id.textView27);
            categoriesTextView = itemView.findViewById(R.id.textView23);
        }
    }
}