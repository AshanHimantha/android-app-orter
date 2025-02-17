// HomeFragment.java
package lk.jiat.orterclothing.ui.home;

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
import android.widget.TextView;
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

import lk.jiat.orterclothing.CategoryAdapter;
import lk.jiat.orterclothing.LatestProductActivity;
import lk.jiat.orterclothing.R;
import lk.jiat.orterclothing.databinding.FragmentHomeBinding;
import lk.jiat.orterclothing.model.Category;
import lk.jiat.orterclothing.model.Product;
import lk.jiat.orterclothing.ProductAdapter; // Import the adapter
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
            "https://firebasestorage.googleapis.com/v0/b/orterclothing-5d6b9.firebasestorage.app/o/412629477_923051502766690_5664355034986246408_n.jpg?alt=media&token=31fd4321-ff92-4f8f-a763-aa31846af020",
            "https://firebasestorage.googleapis.com/v0/b/orterclothing-5d6b9.firebasestorage.app/o/div10.jpg?alt=media&token=6dac3cf0-2d1d-489b-9643-cb1496c05485",
            "https://firebasestorage.googleapis.com/v0/b/orterclothing-5d6b9.firebasestorage.app/o/div8.jpg?alt=media&token=44d3622a-123e-4920-8114-044099a6737e"
    );

    private Animation fadeIn;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private HomeViewModel homeViewModel;
    private static final String API_URL = "http://10.0.2.2:8000/api/stock-list";
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;

    private TextView latestItems;
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
        categoryList.add(new Category(R.drawable.bracelet, "Other", "Unisex Accessories Collection", "15"));


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

                    SharedPreferences sharedPreferences = getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                    boolean isVerified = sharedPreferences.getBoolean("is_verified", false);

                   if (!isVerified) {
                        verifyUser(idToken);
                    }else {
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


}

