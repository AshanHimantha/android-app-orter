package lk.jiat.orterclothing;

import android.content.Intent;
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

public class CategoryViewActivity extends AppCompatActivity {

    private ProductAdapter productAdapter;

    private static final String API_BASE_URL = "https://testapi.ashanhimantha.com/api"; // Adjust if needed
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_view);  // Ensure this layout exists

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView
        RecyclerView categoryItemsRecyclerView = findViewById(R.id.categoryItems); // Correct ID
        categoryItemsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Adjust span count as needed

        // Initialize Adapter
        productAdapter = new ProductAdapter(this, new ArrayList<>()); // Initialize with an empty list
        categoryItemsRecyclerView.setAdapter(productAdapter);


        // Get data from Intent
        Intent intent = getIntent();
        String categoryId = intent.getStringExtra("category_id");
        String categoryName = intent.getStringExtra("category_name");
        String categoryDescription = intent.getStringExtra("description");

        // Set Title and Description in the layout
        TextView categoryTitleTextView = findViewById(R.id.textView33);
        TextView categoryDescriptionTextView = findViewById(R.id.textView37);

        categoryTitleTextView.setText(categoryName);
        categoryDescriptionTextView.setText(categoryDescription);


        TextView backButton = findViewById(R.id.textView38);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Log.d("CategoryViewActivity", "category_id: " + categoryId);
        // Load products for the category
        loadProductsByCategory(categoryId);
    }

    private void loadProductsByCategory(String categoryId) {
        // Construct the API URL to fetch products for the specific category.
        // Adjust this URL to match *your* backend API's requirements.  This is a key change.
        String apiUrl = API_BASE_URL + "/stocks/category/" + categoryId; // Example: API_BASE_URL/stock-list/{categoryID}

        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API_ERROR", "Request Failed: " + e.getMessage());
                runOnUiThread(() -> {
                    Toast.makeText(CategoryViewActivity.this, "Failed to load products.", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CategoryViewActivity.this, "Failed to load products. API Error.", Toast.LENGTH_SHORT).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(CategoryViewActivity.this, "Failed to load products. Response not successful.", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}