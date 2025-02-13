// HomeFragment.java
package lk.jiat.orter.ui.home;

import android.content.res.Configuration;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lk.jiat.orter.CategoryAdapter;
import lk.jiat.orter.R;
import lk.jiat.orter.databinding.FragmentHomeBinding;
import lk.jiat.orter.model.Category;
import lk.jiat.orter.model.Product;
import lk.jiat.orter.ProductAdapter; // Import the adapter
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import androidx.lifecycle.ViewModelProvider;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Handler handler;
    private Runnable runnable;
    private int currentIndex = 0;
    private List<String> imageUrls = Arrays.asList(
            "https://firebasestorage.googleapis.com/v0/b/orter-6f205.firebasestorage.app/o/banner.jpg?alt=media&token=8206f03d-9058-44ae-a25a-65af98b71bbd",
            "https://firebasestorage.googleapis.com/v0/b/orter-6f205.firebasestorage.app/o/div10.jpg?alt=media&token=990d55f7-cbd2-45e0-8c02-90d55b05e11f",
            "https://firebasestorage.googleapis.com/v0/b/orter-6f205.firebasestorage.app/o/div8.jpg?alt=media&token=e68239b5-02a4-48e9-b4ab-1b5abfd3b827"
    );

    private Animation fadeIn;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private HomeViewModel homeViewModel;
    private static final String API_URL = "http://10.0.2.2:8000/api/stock-list";
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;

    private final OkHttpClient client = new OkHttpClient();
    private final String BACKEND_URL = "http://10.0.2.2:8000/api/verify";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Load the fade-in animation
        fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        // Initialize RecyclerView
        recyclerView = binding.getRoot().findViewById(R.id.recyclerView);

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Set Adapter
        productAdapter = new ProductAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(productAdapter);
        int orientation = getResources().getConfiguration().orientation;
        int spanCount = (orientation == Configuration.ORIENTATION_LANDSCAPE) ? 4 : 2;

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        // Observe product list
        homeViewModel.getProductList().observe(getViewLifecycleOwner(), products -> {
            productAdapter.setProductList(products);
            productAdapter.notifyDataSetChanged();
        });

        // Load products if not already loaded
        if (homeViewModel.getProductList().getValue().isEmpty()) {
            loadProducts();
        }

        // Image Slider Handler
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                loadImageWithAnimation();
                handler.postDelayed(this, 5000);
            }
        };
        handler.post(runnable);

        binding.imageView6.setOnClickListener(v -> loadImageWithAnimation());

        categoryRecyclerView = binding.getRoot().findViewById(R.id.recyclerView2);

        // Create a list of categories (replace with your actual data)
        categoryList = new ArrayList<>();
        categoryList.add(new Category(R.drawable.tshirt, "T-Shirts", "Unisex T-Shirts Collection", "16"));
        categoryList.add(new Category(R.drawable.shirt, "Shirts", "Men's Shirts Collection", "2"));
        categoryList.add(new Category(R.drawable.trousers, "Pants", "Men's Pants Collection", "3"));
        categoryList.add(new Category(R.drawable.dress, "Dresses", "Women's Dresses Collection", "6"));
        categoryList.add(new Category(R.drawable.womensshirt, "Tops", "Women's Tops Collection", "7"));
        categoryList.add(new Category(R.drawable.skirt, "Skirts", "Women's Skirts Collection", "8"));
        categoryList.add(new Category(R.drawable.hoodie, "Hoodies", "Unisex Hoodies Collection", "11"));
        categoryList.add(new Category(R.drawable.coat, "Jackets", "Unisex Jackets Collection", "12"));
        categoryList.add(new Category(R.drawable.bag, "Bags", "Unisex Bags Collection", "14"));
        categoryList.add(new Category(R.drawable.bracelet, "Accessories", "Unisex Accessories Collection", "15"));


        // Create the adapter
        categoryAdapter = new CategoryAdapter(categoryList);

        // Set the layout manager (horizontal)
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        categoryRecyclerView.setLayoutManager(layoutManager);

        // Set the adapter
        categoryRecyclerView.setAdapter(categoryAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String idToken = task.getResult().getToken();
                    verifyUser(idToken);
                } else {
                    Log.e("auth", "Error getting ID token", task.getException());
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Authentication error.", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }

        return root;
    }

    private void loadImageWithAnimation() {
        currentIndex = (currentIndex + 1) % imageUrls.size();
Glide.with(this)
    .load(imageUrls.get(currentIndex))
    .into(binding.imageView6);

        binding.imageView6.startAnimation(fadeIn);
    }

    private void loadProducts() {
        List<Product> productList = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API_ERROR", "Request Failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Log.d("API_RESPONSE", responseBody);

                    // Parse JSON using Gson
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

                    if (jsonObject.get("status").getAsBoolean()) {
                        JsonArray dataArray = jsonObject.getAsJsonArray("data");

                        // Loop through API data
                        for (JsonElement item : dataArray) {
                            JsonObject productObject = item.getAsJsonObject();

                            String id = productObject.get("id").getAsString();
                            String productName = productObject.get("product_name").getAsString();
                            String imageUrl = productObject.get("main_image").getAsString();
                            int price = (int) Double.parseDouble(productObject.get("price").getAsString());
                            String collectionName = productObject.get("collection_name").getAsString();

                            productList.add(new Product(id, productName, imageUrl, price, collectionName));
                        }

                        // Update ViewModel with new product list
                        homeViewModel.setProductList(productList);
                    }
                }
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
        binding = null;
    }



    // HomeFragment.java
    private void verifyUser(String idToken) {
        Log.d("auth", "Verifying user with backend...");
        new Thread(() -> {
            try {
                URL url = new URL(BACKEND_URL);
                RequestBody body = RequestBody.create("{}".getBytes(), MediaType.parse("application/json; charset=utf-8"));

                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .header("Content-Type", "application/json; charset=utf-8")
                        .header("Authorization", "Bearer " + idToken)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("auth", "Error verifying user", e);
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Network error verifying user.", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Log.d("auth", "User verified successfully");
                            getActivity().runOnUiThread(() -> {
                                // Handle successful verification
                            });
                        } else {
                            String responseBody = response.body() != null ? response.body().string() : "No body";
                            Log.e("auth", "User verification failed. Response code: " + response.code() + ", Body: " + responseBody);
                            getActivity().runOnUiThread(() -> {
                                Toast.makeText(getContext(), "User verification failed.", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("auth", "Error during verification process", e);
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Error during verification process.", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }


}

