package lk.jiat.orter.ui.home;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lk.jiat.orter.R;
import lk.jiat.orter.databinding.FragmentHomeBinding;
import lk.jiat.orter.model.Product;
import lk.jiat.orter.ProductAdapter; // Import the adapter
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Handler handler;
    private Runnable runnable;
    private int currentIndex = 0;
    private List<String> imageUrls = Arrays.asList(
            "https://orterclothing.com/assets/div10.jpg",
            "https://orterclothing.com/assets/div8.jpg",
            "https://orterclothing.com/assets/collection/Techno Demon Oversized tee_cover_66476d9f32dd3.jpg"
    );

    private Animation fadeIn;
    private RecyclerView recyclerView;
    private List<Product> productList;
    private ProductAdapter productAdapter;
    private static final String API_URL = "http://10.0.2.2:8000/api/stock-list";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Load the fade-in animation
        fadeIn = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);

        // Initialize RecyclerView
        recyclerView = binding.getRoot().findViewById(R.id.recyclerView);
        productList = new ArrayList<>();

        // Load Sample Products
        loadProducts();

        // Set Adapter
        productAdapter = new ProductAdapter(getContext(), productList);
        recyclerView.setAdapter(productAdapter);
        int orientation = getResources().getConfiguration().orientation;
        int spanCount = (orientation == Configuration.ORIENTATION_LANDSCAPE) ? 4 : 2;

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        // Notify adapter of data changes
        productAdapter.notifyDataSetChanged();

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
        productList.add(new Product("Techno Demon Tee", "https://orterclothing.com/assets/div10.jpg", 2599, "Techno Demon"));

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

                            String productName = productObject.get("product_name").getAsString();
                            String imageUrl = productObject.get("main_image").getAsString();
                            int price = (int) Double.parseDouble(productObject.get("price").getAsString());
                            String collectionName = productObject.get("collection_name").getAsString();

                            productList.add(new Product(productName, imageUrl, price, collectionName));
                        }


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
}