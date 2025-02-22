package lk.jiat.orterclothing.ui.order;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.jiat.orterclothing.OrderDetailedActivity;
import lk.jiat.orterclothing.R;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OnOrderClickListener listener;

    public OrderAdapter(Context context, List<Order> orderList, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_list_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderNumber.setText(order.getOrderNumber());

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(order.getOrderDate());
            holder.orderDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            holder.orderDate.setText(order.getOrderDate());
        }

        String status = order.getOrderStatus();
        String displayStatus = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
        holder.orderStatus.setText(displayStatus);

        int colorResId;
        int textColorResId;
        switch (status.toLowerCase()) {
            case "pending":
                colorResId = R.color.pending;
                textColorResId = R.color.pendingText;
                break;
            case "confirmed":
                colorResId = R.color.confirmed;
                textColorResId = R.color.confirmedText;
                break;
            case "shipped":
                colorResId = R.color.shipped;
                textColorResId = R.color.shippedText;
                break;
            case "delivered":
                colorResId = R.color.delivered;
                textColorResId = R.color.deliveredText;
                break;
            case "cancelled":
                colorResId = R.color.cancelled;
                textColorResId = R.color.cancelledText;
                break;
            case "returned":
                colorResId = R.color.returned;
                textColorResId = R.color.returnedText;
                break;
            case "processing":
                colorResId = R.color.processing;
                textColorResId = R.color.processingText;
                break;
            case "completed":
                colorResId = R.color.completed;
                textColorResId = R.color.completedText;
                break;
            default:
                colorResId = R.color.gray;
                textColorResId = android.R.color.black;
        }
        holder.orderStatus.setChipBackgroundColorResource(colorResId);
        holder.orderStatus.setTextColor(context.getResources().getColor(textColorResId));

        holder.productName.setText(order.getProductName());
        holder.items.setText(order.getItems());
        holder.orderTotal.setText("Rs." + order.getOrderTotal());



if (order.getUrl().equals("empty") || order.getUrl().contains("withoutTracking") || status.equalsIgnoreCase("returned") || status.equalsIgnoreCase("cancelled")) {
    holder.trackOrder.setVisibility(View.GONE);
} else {
    holder.trackOrder.setVisibility(View.VISIBLE);
}

        Glide.with(context)
                .load("https://testapi.ashanhimantha.com/storage/" + order.getProductImage())
                .placeholder(R.drawable.div4)
                .into(holder.productImage);

        holder.trackOrder.setOnClickListener(v -> {
            if (listener != null) listener.onTrackOrderClick(order);
        });

        holder.viewDetails.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailedActivity.class);
            intent.putExtra("id", order.getOrderID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public interface OnOrderClickListener {
        void onTrackOrderClick(Order order);

        void onViewDetailsClick(Order order);
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, orderDate, productName, items, orderTotal;
        TextView trackOrder, viewDetails;
        Chip orderStatus;
        ImageView productImage;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.textView66);
            orderDate = itemView.findViewById(R.id.textView69);
            orderStatus = itemView.findViewById(R.id.chip4);
            productName = itemView.findViewById(R.id.textView70);
            items = itemView.findViewById(R.id.textView74);
            orderTotal = itemView.findViewById(R.id.textView71);
            productImage = itemView.findViewById(R.id.imageView5);
            trackOrder = itemView.findViewById(R.id.textView75);
            viewDetails = itemView.findViewById(R.id.textView76);

        }
    }
}