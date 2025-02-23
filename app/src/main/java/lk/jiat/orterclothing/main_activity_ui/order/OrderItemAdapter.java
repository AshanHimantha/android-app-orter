package lk.jiat.orterclothing.main_activity_ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import lk.jiat.orterclothing.R;
import lk.jiat.orterclothing.model.OrderTableItem;


public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
    private List<OrderTableItem> orderItems;

    public OrderItemAdapter(List<OrderTableItem> orderItems) {
        this.orderItems = orderItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_table_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderTableItem item = orderItems.get(position);
        holder.productName.setText(item.getProductName());
        holder.size.setText("Size : "+item.getSize());
        holder.quantity.setText(String.valueOf(item.getQuantity())+"x");
        holder.total.setText(String.valueOf(item.getTotal()));
        Glide.with(holder.imageView.getContext())
                .load("https://testapi.ashanhimantha.com/storage/"+item.getImageUrl())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return orderItems != null ? orderItems.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, size, quantity, total;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.textView78);
            size = itemView.findViewById(R.id.textView82);
            quantity = itemView.findViewById(R.id.textView86);
            total = itemView.findViewById(R.id.textView105);
            imageView = itemView.findViewById(R.id.order_item_img);
        }
    }
}