package lk.jiat.orterclothing.ui.cart;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import lk.jiat.orterclothing.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItemList;
    private okhttp3.OkHttpClient client;
    private TextView totalText;
    private TextView subtotalText;
    private TextView itemCountText;
    private TextView shippingText;
    private Button checkoutButton; // Add checkout button reference

    private TextView emptyCartText; // Add empty cart text reference

    // Flag to prevent rapid UI updates
    private boolean isUpdating = false;

    public CartAdapter(List<CartItem> cartItemList, TextView totalText, TextView subtotalText, TextView itemCountText, TextView shippingText, Button checkoutButton ,TextView emptyCartText) {
        this.cartItemList = cartItemList;
        this.client = new okhttp3.OkHttpClient();
        this.totalText = totalText;
        this.subtotalText = subtotalText;
        this.itemCountText = itemCountText;
        this.shippingText = shippingText;
        this.checkoutButton = checkoutButton; // Initialize the checkout button
        this.emptyCartText = emptyCartText; // Initialize the empty cart text
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


        holder.removeButton.setOnClickListener(v -> {
            for (int i = 0; i < cartItemList.size(); i++) {
                if (cartItemList.get(i).getId() == cartItem.getId()) {
                    cartItemList.remove(i);
                    notifyItemRemoved(i);

                    client.newCall(new Request.Builder()
                            .url("http://10.0.2.2:8000/api/carts/" + cartItem.getId())
                            .header("Authorization", "Bearer " + FirebaseAuth.getInstance().getCurrentUser().getIdToken(false).getResult().getToken())
                            .delete()
                            .build()).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("DELETE_ERROR", "Request Failed: " + e.getMessage());
                            new Handler(Looper.getMainLooper()).post(() ->
                                    Toast.makeText(holder.itemView.getContext(), "Failed to remove item.", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(Call call, Response response) {
                            try {
                                if (response.isSuccessful()) {
                                    Log.d("DELETE_SUCCESS", "Item removed successfully");
                                    String responseBody = response.body().string();
                                    Log.d("DELETE_RESPONSE", responseBody);
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        updateCartSummary(responseBody);
                                    });

                                } else {
                                    Log.e("DELETE_ERROR", "Request Failed: " + response.message());
                                    new Handler(Looper.getMainLooper()).post(() ->
                                            Toast.makeText(holder.itemView.getContext(), "Failed to remove item.", Toast.LENGTH_SHORT).show());
                                }
                            } catch (IOException e) {
                                Log.e("DELETE_ERROR", "IO Exception reading response: " + e.getMessage());
                                new Handler(Looper.getMainLooper()).post(() ->
                                        Toast.makeText(holder.itemView.getContext(), "Failed to read server response.", Toast.LENGTH_SHORT).show());
                            } finally {
                                if (response.body() != null) {
                                    response.body().close(); // Ensure body is closed
                                }
                            }
                        }
                    });
                    break;
                }
            }
        });

        holder.qtyIncButton.setOnClickListener(v -> {
            if (!isUpdating) {
                int newQuantity = cartItem.getQuantity() + 1;
                setUpdating(true); // Set updating to true

                client.newCall(new Request.Builder()
                        .url("http://10.0.2.2:8000/api/carts/" + cartItem.getId() + "/increase")
                        .header("Authorization", "Bearer " + FirebaseAuth.getInstance().getCurrentUser().getIdToken(false).getResult().getToken())
                        .patch(RequestBody.create(null, new byte[0]))
                        .build()).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("PATCH_ERROR", "Request Failed: " + e.getMessage());
                        new Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(holder.itemView.getContext(), "Failed to increase quantity.", Toast.LENGTH_SHORT).show();
                            setUpdating(false); // set updating to false after failure
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) {
                        try {
                            if (response.isSuccessful()) {
                                Log.d("PATCH_SUCCESS", "Quantity increased successfully");
                                String responseBody = response.body().string();

                                new Handler(Looper.getMainLooper()).post(() -> {
                                    cartItem.setQuantity(newQuantity);
                                    holder.quantityTextView.setText(String.valueOf(newQuantity));
                                    updateCartSummary(responseBody);
                                    setUpdating(false); // Set updating to false after success
                                });
                            } else {
                                Log.e("PATCH_ERROR", "Request Failed: " + response.message());
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    Toast.makeText(holder.itemView.getContext(), "Failed to increase quantity.", Toast.LENGTH_SHORT).show();
                                    setUpdating(false); // set updating to false after failure
                                });
                            }
                        } catch (IOException e) {
                            Log.e("PATCH_ERROR", "IO Exception reading response: " + e.getMessage());
                            new Handler(Looper.getMainLooper()).post(() ->
                                    Toast.makeText(holder.itemView.getContext(), "Failed to read server response.", Toast.LENGTH_SHORT).show());
                        } finally {
                            if (response.body() != null) {
                                response.body().close(); // Ensure body is closed
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(holder.itemView.getContext(), "Please wait...", Toast.LENGTH_SHORT).show();
            }
        });

        holder.qtyDecButton.setOnClickListener(v -> {
            if (!isUpdating) {
                int newQuantity = cartItem.getQuantity() - 1;

                if (newQuantity > 0) {
                    setUpdating(true); // Set updating to true
                    client.newCall(new Request.Builder()
                            .url("http://10.0.2.2:8000/api/carts/" + cartItem.getId() + "/decrease")
                            .header("Authorization", "Bearer " + FirebaseAuth.getInstance().getCurrentUser().getIdToken(false).getResult().getToken())
                            .patch(RequestBody.create(null, new byte[0]))
                            .build()).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("PATCH_ERROR", "Request Failed: " + e.getMessage());
                            new Handler(Looper.getMainLooper()).post(() -> {
                                Toast.makeText(holder.itemView.getContext(), "Failed to decrease quantity.", Toast.LENGTH_SHORT).show();
                                setUpdating(false); // set updating to false after failure
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) {
                            try {
                                if (response.isSuccessful()) {
                                    Log.d("PATCH_SUCCESS", "Quantity decreased successfully");
                                    String responseBody = response.body().string();

                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        cartItem.setQuantity(newQuantity);
                                        holder.quantityTextView.setText(String.valueOf(newQuantity));
                                        updateCartSummary(responseBody);
                                        setUpdating(false); // Set updating to false after success
                                    });
                                } else {
                                    Log.e("PATCH_ERROR", "Request Failed: " + response.message());
                                    new Handler(Looper.getMainLooper()).post(() -> {
                                        Toast.makeText(holder.itemView.getContext(), "Failed to decrease quantity.", Toast.LENGTH_SHORT).show();
                                        setUpdating(false); // set updating to false after failure
                                    });
                                }
                            } catch (IOException e) {
                                Log.e("PATCH_ERROR", "IO Exception reading response: " + e.getMessage());
                                new Handler(Looper.getMainLooper()).post(() ->
                                        Toast.makeText(holder.itemView.getContext(), "Failed to read server response.", Toast.LENGTH_SHORT).show());
                            } finally {
                                if (response.body() != null) {
                                    response.body().close(); // Ensure body is closed
                                }
                            }
                        }
                    });
                }
            } else {
                Toast.makeText(holder.itemView.getContext(), "Please wait...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCartSummary(String responseBody) {
        try {
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONObject summary = jsonResponse.getJSONObject("summary");

            int itemCount = summary.getInt("item_count");
            int subtotal = summary.getInt("subtotal");
            int shippingFee = summary.getInt("shipping_fee");
            int total = summary.getInt("total");

            totalText.setText(String.valueOf(total));
            subtotalText.setText(String.valueOf(subtotal));
            itemCountText.setText(String.valueOf(itemCount));
            shippingText.setText(String.valueOf(shippingFee));

            // Disable checkout button if cart is empty
             checkoutButton.setEnabled(itemCount > 0);
            checkoutButton.setBackgroundColor(itemCount > 0 ? ContextCompat.getColor(checkoutButton.getContext(), R.color.black) : ContextCompat.getColor(checkoutButton.getContext(), R.color.gray));
            emptyCartText.setVisibility(itemCount > 0 ? View.GONE : View.VISIBLE);
        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Failed to parse JSON: " + e.getMessage());
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(totalText.getContext(), "Failed to parse cart summary.", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextView, quantityTextView, sizeTextView, categoriesTextView, removeButton, qtyIncButton, qtyDecButton;
        RoundedImageView imageView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textView9);
            priceTextView = itemView.findViewById(R.id.textView21);
            quantityTextView = itemView.findViewById(R.id.textView25);
            imageView = itemView.findViewById(R.id.imageView5);
            sizeTextView = itemView.findViewById(R.id.textView27);
            categoriesTextView = itemView.findViewById(R.id.textView23);
            removeButton = itemView.findViewById(R.id.textView22);
            qtyIncButton = itemView.findViewById(R.id.textView26);
            qtyDecButton = itemView.findViewById(R.id.textView24);
        }
    }

    // Method to set the updating flag
    public synchronized void setUpdating(boolean updating) {
        isUpdating = updating;
    }

    // Method to get the updating flag
    public synchronized boolean isUpdating() {
        return isUpdating;
    }
}