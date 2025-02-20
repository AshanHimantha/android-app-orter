package lk.jiat.orterclothing;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lk.jiat.orterclothing.model.Product;  // Make sure this import is correct
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LatestProductActivity extends AppCompatActivity {

    private RecyclerView latestItemsRecyclerView;
    private ProductAdapter productAdapter;  // Use the same adapter from HomeFragment


    private static final String API_BASE_URL = "https://testapi.ashanhimantha.com/api";
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_latest_product);  // Ensure this layout exists

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView
        latestItemsRecyclerView = findViewById(R.id.latestItems); // Correct ID
        latestItemsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Adjust span count as needed

        // Initialize Adapter
        productAdapter = new ProductAdapter(this, new ArrayList<>()); // Initialize with an empty list
        latestItemsRecyclerView.setAdapter(productAdapter);


        TextView backButton = findViewById(R.id.textView38);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        loadProducts();
    }

    private void loadProducts() {

        String apiUrl = API_BASE_URL + "/latest-stocks";

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API_ERROR", "Request Failed: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(LatestProductActivity.this, "Failed to load products.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    Log.d("API_RESPONSE", responseBody);

                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);

                    if (jsonObject.get("status").getAsBoolean()) {
                        JsonArray dataArray = jsonObject.getAsJsonArray("data");
                        List<Product> productList = new ArrayList<>();

                        for (JsonElement item : dataArray) {
                            JsonObject productObject = item.getAsJsonObject();

                            String id = productObject.get("id").getAsString();
                            String productName = productObject.get("product_name").getAsString();
                            String imageUrl = productObject.get("main_image").getAsString();
                            int price = (int) Double.parseDouble(productObject.get("price").getAsString());
                            String collectionName = productObject.get("collection_name").getAsString();

                            productList.add(new Product(id, productName, imageUrl, price, collectionName));
                        }

                        // Update the adapter on the main thread
                        runOnUiThread(() -> {
                            productAdapter.setProductList(productList);
                            productAdapter.notifyDataSetChanged();
                        });

                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(LatestProductActivity.this, "Failed to load products. API Error.", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(LatestProductActivity.this, "Failed to load products. Response not successful.", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}