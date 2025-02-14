package lk.jiat.orterclothing;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SingleProductActivity extends AppCompatActivity {

    private Button sizeXSButton;
    private Button sizeSButton;
    private Button sizeMButton;
    private Button sizeLButton;
    private Button sizeXLButton;
    private Button sizeXXLButton;

    private String selectedSize;
    private String productId;
    private int stockId;

    private TextView productTitleTextView;
    private TextView productPriceTextView;
    private TextView productDescriptionTextView;
    private TextView materialDescriptionTextView;
    private ImageView productImageView;
    private TextView availableItemsTextView;

    private ImageView image1;
    private ImageView image2;
    private ImageView image3;

    private int xsQuantity = 0;
    private int sQuantity = 0;
    private int mQuantity = 0;
    private int lQuantity = 0;
    private int xlQuantity = 0;
    private int xxlQuantity = 0;


    private final OkHttpClient client = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        productTitleTextView = findViewById(R.id.product_title);
        productPriceTextView = findViewById(R.id.product_price);
        productDescriptionTextView = findViewById(R.id.product_description);
        materialDescriptionTextView = findViewById(R.id.material_description);
        productImageView = findViewById(R.id.imageView6); // The main image view
        availableItemsTextView = findViewById(R.id.available_items); // Initialize available items TextView

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);

        Intent intent = getIntent();
        productId = intent.getStringExtra("product");

        // Load product details
        if (productId != null) {
            loadProductDetails(productId);
        } else {
            Toast.makeText(this, "Product ID not found.", Toast.LENGTH_SHORT).show();
        }

        Button back = findViewById(R.id.button3);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        sizeXSButton = findViewById(R.id.size_xs);
        sizeSButton = findViewById(R.id.size_s);
        sizeMButton = findViewById(R.id.size_m);
        sizeLButton = findViewById(R.id.size_l);
        sizeXLButton = findViewById(R.id.size_xl);
        sizeXXLButton = findViewById(R.id.size_xxl);

        sizeXSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSize("XS");
                updateAvailableItems(xsQuantity);
            }
        });
        sizeSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSize("S");
                updateAvailableItems(sQuantity);
            }
        });
        sizeMButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSize("M");
                updateAvailableItems(mQuantity);
            }
        });
        sizeLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSize("L");
                updateAvailableItems(lQuantity);
            }
        });
        sizeXLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSize("XL");
                updateAvailableItems(xlQuantity);
            }
        });
        sizeXXLButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSize("XXL");
                updateAvailableItems(xxlQuantity);
            }
        });

        Button addToCartButton = findViewById(R.id.add_to_cart_button);
        addToCartButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AddToCart();

            }
        });

    }

    private void loadProductDetails(String productId) {
        String url = "http://10.0.2.2:8000/api/stocks/" + productId;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("SingleProductActivity", "OkHttp error: " + e.getMessage());
                mainHandler.post(() -> // Use Handler to update UI on the main thread
                        Toast.makeText(SingleProductActivity.this, "Failed to load product details.", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();

                    mainHandler.post(() -> { // Use Handler to update UI on the main thread
                        try {
                            // Parse JSON data based on the new response structure
                            JSONObject jsonResponse = new JSONObject(responseBody);

                            if (jsonResponse.getBoolean("status")) {
                                JSONObject data = jsonResponse.getJSONObject("data");
                                JSONObject product = data.getJSONObject("product");

                                String title = product.getString("name");
                                String price = product.getString("price"); // Price is now a String
                                String description = product.getString("description");
                                String material = product.getString("material");
                                String mainImageUrl = product.getString("main_image_url");
                                String image1Url = product.getString("image_1_url");
                                String image2Url = product.getString("image_2_url");

                                // Get quantities
                                xsQuantity = data.getInt("xs_quantity");
                                sQuantity = data.getInt("s_quantity");
                                mQuantity = data.getInt("m_quantity");
                                lQuantity = data.getInt("l_quantity");
                                xlQuantity = data.getInt("xl_quantity");
                                xxlQuantity = data.getInt("xxl_quantity");

                                // Update UI elements
                                productTitleTextView.setText(title);
                                productPriceTextView.setText("Rs. " + price); // Display price as is
                                productDescriptionTextView.setText(description);
                                materialDescriptionTextView.setText(material);


                                        Glide.with(SingleProductActivity.this)
                                                .load(mainImageUrl)
                                                .into(productImageView);

                                        // Load other images with placeholders
                                        Glide.with(SingleProductActivity.this)
                                                .load(image1Url)
                                                .into(image1);

                                        Glide.with(SingleProductActivity.this)
                                                .load(image2Url)
                                                .into(image2);

                                        Glide.with(SingleProductActivity.this)
                                                .load(mainImageUrl)
                                                .into(image3);





                                // Set onClickListeners for changing main image
                                image1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Glide.with(SingleProductActivity.this).load(image1Url).into(productImageView);
                                    }
                                });

                                image2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Glide.with(SingleProductActivity.this).load(image2Url).into(productImageView);
                                    }
                                });

                                image3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Glide.with(SingleProductActivity.this).load(mainImageUrl).into(productImageView);
                                    }
                                });


                                // Initially update the available items count for the first size (e.g., XS)
                                updateAvailableItems(xsQuantity);

                                // Disable buttons if quantity is 0
                                disableButtonIfNoQuantity(sizeXSButton, xsQuantity);
                                disableButtonIfNoQuantity(sizeSButton, sQuantity);
                                disableButtonIfNoQuantity(sizeMButton, mQuantity);
                                disableButtonIfNoQuantity(sizeLButton, lQuantity);
                                disableButtonIfNoQuantity(sizeXLButton, xlQuantity);
                                disableButtonIfNoQuantity(sizeXXLButton, xxlQuantity);

                            } else {
                                Log.e("SingleProductActivity", "API returned status: false");
                                Toast.makeText(SingleProductActivity.this, "Failed to load product details.", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            Log.e("SingleProductActivity", "Error parsing JSON: " + e.getMessage());
                            Toast.makeText(SingleProductActivity.this, "Error loading product details.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e("SingleProductActivity", "HTTP error: " + response.code());
                    mainHandler.post(() ->
                            Toast.makeText(SingleProductActivity.this, "Failed to load product details (HTTP " + response.code() + ").", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void updateAvailableItems(int quantity) {
        availableItemsTextView.setText("Available Items: " + quantity);
    }

    private void disableButtonIfNoQuantity(Button button, int quantity) {
        if (quantity == 0) {
            button.setEnabled(false);
            button.setBackgroundColor(Color.GRAY);
            button.setTextColor(Color.DKGRAY);
        } else {
            button.setEnabled(true);
        }
    }

    private void setSize(String size) {
        // Update selectedSize variable
        selectedSize = size;

        // Update UI: Deselect all buttons, then select the clicked one
        resetButtonStyles();

        //After resetting the styles, we need to disable the button again, if its quantity is 0
        disableButtonIfNoQuantity(sizeXSButton, xsQuantity);
        disableButtonIfNoQuantity(sizeSButton, sQuantity);
        disableButtonIfNoQuantity(sizeMButton, mQuantity);
        disableButtonIfNoQuantity(sizeLButton, lQuantity);
        disableButtonIfNoQuantity(sizeXLButton, xlQuantity);
        disableButtonIfNoQuantity(sizeXXLButton, xxlQuantity);


        switch (size) {
            case "XS":
                selectButton(sizeXSButton);
                break;
            case "S":
                selectButton(sizeSButton);
                break;
            case "M":
                selectButton(sizeMButton);
                break;
            case "L":
                selectButton(sizeLButton);
                break;
            case "XL":
                selectButton(sizeXLButton);
                break;
            case "XXL":
                selectButton(sizeXXLButton);
                break;
        }

        // Now you can use the 'selectedSize' variable. For example:
        System.out.println("Selected size: " + selectedSize);
    }

    private void resetButtonStyles() {
        // Reset background color and text color for all buttons
        sizeXSButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white)); // Or whatever your default background color is
        sizeSButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        sizeMButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        sizeLButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        sizeXLButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        sizeXXLButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white));

        sizeXSButton.setTextColor(ContextCompat.getColor(this, R.color.black)); // Or whatever your default text color is
        sizeSButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        sizeMButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        sizeLButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        sizeXLButton.setTextColor(ContextCompat.getColor(this, R.color.black));
        sizeXXLButton.setTextColor(ContextCompat.getColor(this, R.color.black));



    }

    private void selectButton(Button button) {
        // Change the appearance of the selected button
        button.setBackgroundColor(ContextCompat.getColor(this, R.color.black)); // Or whatever color you want
        button.setTextColor(ContextCompat.getColor(this, R.color.white)); // Or whatever color you want
    }

    private void AddToCart() {
        if (selectedSize == null) {
            Toast.makeText(this, "Please select a size.", Toast.LENGTH_SHORT).show();
            return;
        }else {

            Button addToCartButton = findViewById(R.id.add_to_cart_button);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addToCartButton.setEnabled(false);
                    addToCartButton.setText("Adding to cart...");
                    addToCartButton.setBackgroundColor(Color.GRAY);

                }
            });

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            String url = "http://10.0.2.2:8000/api/carts";
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put("firebase_uid", currentUser.getUid());
                jsonObject.put("stock_id", productId);
                jsonObject.put("size", selectedSize);
                jsonObject.put("quantity", "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Authorization", "Bearer " + currentUser.getIdToken(false).getResult().getToken())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("SingleProductActivity", "OkHttp error: " + e.getMessage());
                    mainHandler.post(() ->
                            Toast.makeText(SingleProductActivity.this, "Failed to add to cart.", Toast.LENGTH_SHORT).show());

                    Button addToCartButton = findViewById(R.id.add_to_cart_button);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addToCartButton.setEnabled(true);
                            addToCartButton.setText("Adding to cart...");
                            addToCartButton.setBackgroundColor(Color.BLACK);


                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        mainHandler.post(() ->
                                Toast.makeText(SingleProductActivity.this, "Added to cart successfully.", Toast.LENGTH_SHORT).show());
                        Button addToCartButton = findViewById(R.id.add_to_cart_button);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addToCartButton.setEnabled(true);
                                addToCartButton.setText("Adding to cart");
                                addToCartButton.setBackgroundColor(Color.BLACK);


                            }
                        });
                    } else {
                        Log.e("SingleProductActivity", "HTTP error: " + response.code());
                        Log.e("SingleProductActivity", "Response: " + response.body().string());
                        mainHandler.post(() ->
                                Toast.makeText(SingleProductActivity.this, "Failed to add to cart (HTTP " + response.code() + ").", Toast.LENGTH_SHORT).show());

                        Button addToCartButton = findViewById(R.id.add_to_cart_button);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addToCartButton.setEnabled(true);
                                addToCartButton.setText("Adding to cart");
                                addToCartButton.setBackgroundColor(Color.BLACK);


                            }
                        });



                    }
                }
            });

        }
    }
}