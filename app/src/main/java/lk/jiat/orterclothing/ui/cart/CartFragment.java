package lk.jiat.orterclothing.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lk.jiat.orterclothing.CheckoutActivity;
import lk.jiat.orterclothing.LoginActivity;
import lk.jiat.orterclothing.R;
import lk.jiat.orterclothing.databinding.FragmentCartBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CartFragment extends Fragment {

    private FragmentCartBinding binding;
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private static final String API_URL = "http://10.0.2.2:8000/api/user-cart";
    private final OkHttpClient client = new OkHttpClient();

    private TextView totalText;
    private TextView subtotalText;
    private TextView itemCountText;
    private TextView shippingText;
    private ProgressBar progressBar;

    private Button checkoutButton;

    private TextView EmptyCartText;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        totalText = root.findViewById(R.id.textView20);
        subtotalText = root.findViewById(R.id.textView15);
        itemCountText = root.findViewById(R.id.textView18);
        shippingText = root.findViewById(R.id.textView30);
        progressBar = root.findViewById(R.id.progressBar);
        checkoutButton = root.findViewById(R.id.button4);
        EmptyCartText = root.findViewById(R.id.textView14);



        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize cart item list and adapter
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList, totalText, subtotalText, itemCountText, shippingText,checkoutButton, EmptyCartText);

        recyclerView.setAdapter(cartAdapter);
        setupSwipeToDelete();

        EmptyCartText.setVisibility(View.GONE);
        checkoutButton.setEnabled(false);
        checkoutButton.setBackgroundColor(ContextCompat.getColor(checkoutButton.getContext(), R.color.gray));


        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CheckoutActivity.class);
                intent.putExtra("total", totalText.getText().toString());
                intent.putExtra("itemCount", itemCountText.getText().toString());
                intent.putExtra("shipping", shippingText.getText().toString());
                intent.putExtra("subtotal", subtotalText.getText().toString());


                startActivity(intent);

            }
        });




        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems(); // Load cart items when the fragment is resumed
    }


private void setupSwipeToDelete() {
    ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            CartItem cartItem = cartItemList.get(position);

            if (!cartAdapter.isUpdating()) {
                Toast.makeText(requireContext(),
                        "Removing " + cartItem.getProduct().getName(),
                        Toast.LENGTH_SHORT).show();

                // Trigger existing remove functionality through the remove button
                CartAdapter.CartViewHolder holder = (CartAdapter.CartViewHolder) viewHolder;
                holder.removeButton.performClick();
            } else {
                // If an update is in progress, revert the swipe

                Toast.makeText(requireContext(), "Please wait...", Toast.LENGTH_SHORT).show();
            }
            cartAdapter.notifyItemChanged(position);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            // Reset any view changes here if needed
            viewHolder.itemView.setAlpha(1.0f);
        }

        @Override
        public void onChildDraw(@NonNull android.graphics.Canvas c, @NonNull RecyclerView recyclerView,
                @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                int actionState, boolean isCurrentlyActive) {
            // Add swipe animation effects here if needed
            viewHolder.itemView.setAlpha(1.0f - Math.abs(dX) / (float) viewHolder.itemView.getWidth());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView);
}

    private void loadCartItems() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            currentUser.getIdToken(true).addOnCompleteListener(task -> { // Force refresh
                if (task.isSuccessful()) {
                    String token = task.getResult().getToken();
                    Log.d("BEARER_TOKEN", "Token: " + token);
                    makeApiRequest(token); // Call makeApiRequest with the token

                } else {
                    Log.e("TOKEN_ERROR", "Failed to get token asynchronously: " + task.getException().getMessage());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->

                                Toast.makeText(getContext(), "Failed to get authentication token.", Toast.LENGTH_SHORT).show());
                    }

                }
            });
        } else {
            Log.e("AUTH_ERROR", "User is not authenticated");
            if (getActivity() != null) {


                           Intent intent2 = new Intent(getContext(), LoginActivity.class);
                           startActivity(intent2);

            }

        }
    }

private void makeApiRequest(String token) {
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String urlWithUid = API_URL + "?firebase_uid=" + currentUser.getUid();
    Request request = new Request.Builder()
            .url(urlWithUid)
            .header("Authorization", "Bearer " + token)
            .header("Accept", "application/json")
            .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API_ERROR", "Request Failed: " + e.getMessage());
                if (getActivity() != null) { // Check if activity is still valid
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to load cart items.", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("API_RESPONSE", response.toString());

                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Log.d("API_RESPONSE", responseBody);

                    // Parse JSON using Gson
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);


                    String total = jsonObject.get("total_amount").getAsString();
                    String subtotal = jsonObject.get("sub_total").getAsString();
                    String itemCount = jsonObject.get("total_items").getAsString();
                    String shipping = jsonObject.get("courier_charge").getAsString();


                    if (jsonObject.get("status").getAsBoolean()) {
                        JsonArray dataArray = jsonObject.getAsJsonArray("data");
                        List<CartItem> newCartItemList = new ArrayList<>(); // Store parsed data



                        // Loop through API data
                        for (JsonElement item : dataArray) {
                            JsonObject itemObject = item.getAsJsonObject();

                            int id = itemObject.get("id").getAsInt();
                            String size = itemObject.get("size").getAsString();
                            int quantity = itemObject.get("quantity").getAsInt();

                            JsonObject productObject = itemObject.getAsJsonObject("product");
                            String name = productObject.get("name").getAsString();
                            int price = productObject.get("price").getAsInt();
                            String mainImage = productObject.get("main_image").getAsString();
                            String categoryName = productObject.getAsJsonObject("category").get("name").getAsString();

                            CartItem.Product product = new CartItem.Product(name, price, mainImage, categoryName);
                            CartItem cartItem = new CartItem(id, size, quantity, product);

                            newCartItemList.add(cartItem);
                        }

                        // Update RecyclerView on the main thread
                        if (getActivity() != null) { // Check if activity is still valid
                            getActivity().runOnUiThread(() -> {
                                cartItemList.clear(); // Clear old data
                                cartItemList.addAll(newCartItemList); // Add new data
                                subtotalText.setText(subtotal);
                                itemCountText.setText(itemCount);
                                cartAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);


                                if (Integer.parseInt(itemCount) == 0) {
                                    checkoutButton.setEnabled(false);
                                    checkoutButton.setBackgroundColor(ContextCompat.getColor(checkoutButton.getContext(), R.color.gray));
                                    shippingText.setText("0");
                                    totalText.setText("0");
                                    EmptyCartText.setVisibility(View.VISIBLE);
                                } else {
                                    totalText.setText("Rs."+total);
                                    shippingText.setText(shipping);
                                    checkoutButton.setEnabled(true);
                                    checkoutButton.setBackgroundColor(ContextCompat.getColor(checkoutButton.getContext(), R.color.black));
                                }


                            });
                        }
                    } else {
                        Log.e("API_ERROR", "Request Failed: " + response.message());
                        if (getActivity() != null) { // Check if activity is still valid
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getContext(), "Failed to load cart items.", Toast.LENGTH_SHORT).show());
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Request Failed: " + response.message());
                    if (getActivity() != null) { // Check if activity is still valid
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Failed to load cart items.", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}