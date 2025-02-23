
package lk.jiat.orterclothing.main_activity_ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lk.jiat.orterclothing.adpters.CategoryAdapter;
import lk.jiat.orterclothing.LatestProductActivity;
import lk.jiat.orterclothing.R;
import lk.jiat.orterclothing.databinding.FragmentHomeBinding;
import lk.jiat.orterclothing.model.Category;
import lk.jiat.orterclothing.model.Product;
import lk.jiat.orterclothing.adpters.ProductAdapter; // Import the adapter
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
    private List<String> imageUrls;

    private Animation fadeIn;
    private ProductAdapter productAdapter;
    private HomeViewModel homeViewModel;
    private static final String API_URL = "https://testapi.ashanhimantha.com/api/stock-list";

    private NestedScrollView nestedScrollView;

    private ProgressBar progressBar;
    private TextView latestItems;
    private final OkHttpClient client = new OkHttpClient();
    private final String BACKEND_URL = "https://testapi.ashanhimantha.com/api/verify";

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        nestedScrollView = binding.getRoot().findViewById(R.id.nestedScrollView);
        progressBar = binding.getRoot().findViewById(R.id.progressBar);
        nestedScrollView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        // Load the fade-in animation
        fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        // Initialize RecyclerView
        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView);

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        imageUrls = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("banners").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {


                task.getResult().forEach(document -> {
                    String imageUrl = document.getString("image");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        imageUrls.add(imageUrl);
                    }
                });
            }

            // Start image rotation only after loading images
            if (!imageUrls.isEmpty()) {
                handler.post(runnable);
            }
        });

// Move handler initialization after imageUrls setup
        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                loadImageWithAnimation();
                handler.postDelayed(this, 10000);
            }
        };

        TextView seeallCategory = binding.getRoot().findViewById(R.id.textView10);
        seeallCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Change the Intent when textView10 is clicked
                RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView2);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 5)); // Change 3 to your desired span count
            }
        });


        TextView seeall = binding.getRoot().findViewById(R.id.textView8);
        seeall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the Intent when textView39 is clicked
                Intent intent = new Intent(getActivity(), LatestProductActivity.class);
                startActivity(intent);
            }
        });

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
        if (Objects.requireNonNull(homeViewModel.getProductList().getValue()).isEmpty()) {
            loadProducts();
        } else {

            nestedScrollView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }


        handler.post(runnable);

        binding.imageView6.setOnClickListener(v -> loadImageWithAnimation());

        RecyclerView categoryRecyclerView = binding.getRoot().findViewById(R.id.recyclerView2);


        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category(R.drawable.tshirt, "T-Shirts", "Unisex T-Shirts Collection", "16"));
        categoryList.add(new Category(R.drawable.shirt, "Shirts", "Men's Shirts Collection", "2"));
        categoryList.add(new Category(R.drawable.trousers, "Pants", "Men's Pants Collection", "3"));
        categoryList.add(new Category(R.drawable.dress, "Dresses", "Women's Dresses Collection", "6"));
        categoryList.add(new Category(R.drawable.womensshirt, "Tops", "Women's Tops Collection", "7"));
        categoryList.add(new Category(R.drawable.skirt, "Skirts", "Women's Skirts Collection", "8"));
        categoryList.add(new Category(R.drawable.hoodie, "Hoodies", "Unisex Hoodies Collection", "11"));
        categoryList.add(new Category(R.drawable.coat, "Jackets", "Unisex Jackets Collection", "12"));
        categoryList.add(new Category(R.drawable.bag, "Bags", "Unisex Bags Collection", "14"));
        categoryList.add(new Category(R.drawable.bracelet, "Other", "Unisex Accessories Collection", "15"));


        // Create the adapter
        CategoryAdapter categoryAdapter = new CategoryAdapter(categoryList);

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

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    boolean isVerified = sharedPreferences.getBoolean("is_verified", false);

                    if (!isVerified) {
                        verifyUser(idToken);
                    } else {
                        Log.d("auth", "User already verified");
                    }

                } else {
                    Log.e("auth", "Error getting ID token", task.getException());
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Authentication error.", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }


  SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
  long tokenTime = sharedPreferences.getLong("fcm_token_time", 0);
  long currentTime = System.currentTimeMillis();
  long sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000; // 7 days in milliseconds

  if (tokenTime == 0 || (currentTime - tokenTime) > sevenDaysInMillis) {
      verifyFCMToken();
  }









  SharedPreferences test = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
    Log.e("shared", test.getBoolean("is_verified", false) + "");
    Log.e("shared", test.getString("fcm_token", "") + "");
    Log.e("shared", test.getLong("fcm_token_time", 0) + "");



        return root;
    }

    private void loadImageWithAnimation() {
        // Check if list is empty or null
        if (imageUrls == null || imageUrls.isEmpty()) {
            // Load a default image or return
            Glide.with(this)
                    .load(R.drawable.div2)
                    .into(binding.imageView6);
            return;
        }

        currentIndex = (currentIndex + 1) % imageUrls.size();
        Glide.with(this)
                .load(imageUrls.get(currentIndex))
                .error(R.drawable.div2)
                .placeholder(R.drawable.div2)
                .centerCrop()
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

                    getActivity().runOnUiThread(() -> {


                        Animation scaleUp = AnimationUtils.loadAnimation(getContext(), R.anim.scale_up);
                        nestedScrollView.startAnimation(scaleUp);
                        nestedScrollView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);


                    });

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
                            String category = productObject.get("category_id").getAsString();

                            productList.add(new Product(id, productName, imageUrl, price, collectionName,category));

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
                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                            sharedPreferences.edit().putBoolean("is_verified", true).apply();
                            Log.d("shared", "is_verified set to true");
                            Log.e("shared", sharedPreferences.getBoolean("is_verified", false) + "");


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

    private void verifyFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM", "Token: " + token);


                    sendTokenToServer(token);

                });
    }

    private void sendTokenToServer(String token) {

        new Thread(new Runnable() {
            @Override
            public void run() {


                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    currentUser.getIdToken(true).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            OkHttpClient client = new OkHttpClient();
                            RequestBody body = RequestBody.create(
                                    "{\"fcm_token\":\"" + token + "\"}",
                                    MediaType.parse("application/json; charset=utf-8")
                            );
                            Request request = new Request.Builder()
                                    .url("https://testapi.ashanhimantha.com/api/user/fcm-token")
                                    .post(body)
                                    .header("Authorization", "Bearer " + idToken)
                                    .header("Content-Type", "application/json; charset=utf-8")
                                    .build();

                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e("FCM", "Failed to send token to server", e);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        Log.d("FCM", "Token sent successfully");
                                        if (getContext() != null) {
                                            SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                                            sharedPreferences.edit().putString("fcm_token", token).putLong("fcm_token_time", System.currentTimeMillis()).apply();

                                        } else {
                                            Log.e("FCM", "Context is null, cannot save token");
                                        }
                                    } else {
                                        Log.e("FCM", "Failed to send token to server. Response code: " + response);
                                    }
                                }
                            });
                        } else {
                            Log.e("FCM", "Error getting ID token", task.getException());
                        }
                    });
                }
            }
        }).start();

    }
}
