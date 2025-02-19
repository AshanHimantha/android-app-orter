package lk.jiat.orterclothing;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
<<<<<<< HEAD
import android.widget.LinearLayout;
import android.widget.ProgressBar;
=======
>>>>>>> 8df22a4a67e6d2c7632c597099c6c7f051eb41d7
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.jiat.orterclothing.model.OrderTableItem;
import lk.jiat.orterclothing.ui.order.OrderItemAdapter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrderDetailedActivity extends AppCompatActivity {

    private static final String API_URL = "https://testapi.ashanhimantha.com/api/order/";
    private RecyclerView recyclerView;
    private OrderItemAdapter adapter;
    private TextView orderID;
    private TextView cname;
    private TextView email;
    private TextView address;
    private TextView phone;
    private TextView total;
    private TextView status;
    private TextView date;
    private TextView payment;

    private TextView subtotal;
    private TextView items1;

    private TextView shipping;

    private String ID;
    private TextView addressText;
<<<<<<< HEAD
    private TextView pickupText;
    private TextView pickupid;

    ProgressBar progressBar;
    LinearLayout mainLayout;

=======
>>>>>>> 8df22a4a67e6d2c7632c597099c6c7f051eb41d7


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detailed);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        Intent intent = getIntent();
        ID = intent.getStringExtra("id");

        setupRecyclerView();
        fetchOrderDetails();


        orderID = findViewById(R.id.textView68);
        cname = findViewById(R.id.textView88);
//                email = findViewById(R.id.textView94);
        address = findViewById(R.id.textView96);
<<<<<<< HEAD
        phone = findViewById(R.id.textView91);
=======
                phone = findViewById(R.id.textView98);
>>>>>>> 8df22a4a67e6d2c7632c597099c6c7f051eb41d7
        total = findViewById(R.id.textView101);
        status = findViewById(R.id.textView77);
        date = findViewById(R.id.textView104);
        payment = findViewById(R.id.textView102);
        subtotal = findViewById(R.id.textView99);
        items1 = findViewById(R.id.textView81);
        shipping = findViewById(R.id.textView83);
        addressText = findViewById(R.id.textView95);
<<<<<<< HEAD
        pickupText = findViewById(R.id.textView92);
        pickupid = findViewById(R.id.textView93);
        findViewById(R.id.button17).setOnClickListener(v -> finish());
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        mainLayout = findViewById(R.id.linearLayout7);
        mainLayout.setVisibility(LinearLayout.GONE);

=======
>>>>>>> 8df22a4a67e6d2c7632c597099c6c7f051eb41d7



    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.order_item_table);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderItemAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void fetchOrderDetails() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(API_URL+ID)
                .get()
                .addHeader("Accept", "application/json")
                .header("Authorization", "Bearer " + FirebaseAuth.getInstance().getCurrentUser().getIdToken(false).getResult().getToken())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
<<<<<<< HEAD

=======
>>>>>>> 8df22a4a67e6d2c7632c597099c6c7f051eb41d7
                        Toast.makeText(OrderDetailedActivity.this,
                                "Failed to load order details", Toast.LENGTH_SHORT).show()
                );
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call call, Response response) throws IOException {
<<<<<<< HEAD

                runOnUiThread(() -> progressBar.setVisibility(ProgressBar.GONE));
                runOnUiThread(() -> mainLayout.setVisibility(LinearLayout.VISIBLE));

                if (!response.isSuccessful()) {

=======
                if (!response.isSuccessful()) {
>>>>>>> 8df22a4a67e6d2c7632c597099c6c7f051eb41d7
                    runOnUiThread(() ->
                            Toast.makeText(OrderDetailedActivity.this,
                                    "Error: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                    return;
                }

                String responseData = response.body().string();
                try {
                    JSONObject jsonResponse = new JSONObject(responseData);
                    if (jsonResponse.getBoolean("status")) {
                        JSONObject data = jsonResponse.getJSONObject("data");
                        JSONArray items = data.getJSONArray("items");
                        runOnUiThread(() -> {
                            try {
<<<<<<< HEAD


                                orderID.setText(data.getString("orderNumber"));
                                JSONObject deliveryDetails = data.getJSONObject("deliveryDetails");
                                cname.setText(deliveryDetails.getString("name"));
                                phone.setText(deliveryDetails.getString("phone"));
                                address.setText(deliveryDetails.getString("address") + ", " + deliveryDetails.getString("city"));
                                total.setText("Rs. " + data.getString("total"));
                                pickupid.setText(data.getString("pickupId"));
=======
                                orderID.setText(data.getString("orderNumber"));
                                JSONObject deliveryDetails = data.getJSONObject("deliveryDetails");
                                cname.setText(deliveryDetails.getString("name"));
                                address.setText(deliveryDetails.getString("address") + ", " + deliveryDetails.getString("city"));
                                total.setText("Rs. " + data.getString("total"));
>>>>>>> 8df22a4a67e6d2c7632c597099c6c7f051eb41d7
                                status.setText(data.getString("orderStatus").substring(0, 1).toUpperCase() + data.getString("orderStatus").substring(1));
                                try {
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                                    Date parsedDate = inputFormat.parse(data.getString("orderDate"));
                                    date.setText(outputFormat.format(parsedDate));
                                } catch (ParseException e) {
                                    date.setText(data.getString("orderDate"));
                                    Log.e("OrderDetailedActivity", "Error parsing date: " + e.getMessage());
                                }
                                payment.setText(data.getString("deliveryType").substring(0, 1).toUpperCase() + data.getString("deliveryType").substring(1));
                                subtotal.setText("Rs. " + data.getString("subTotal"));
                                items1.setText(data.getJSONArray("items").length() + " item");
                                shipping.setText("Rs. " + data.getString("shippingFee"));


                                if (data.getString("deliveryType").equals("pickup")) {
                                   address.setVisibility(TextView.GONE);
                                   addressText.setVisibility(TextView.GONE);
<<<<<<< HEAD
                                   pickupText.setVisibility(TextView.VISIBLE);

                                }else {
                                    address.setVisibility(TextView.VISIBLE);
                                    addressText.setVisibility(TextView.VISIBLE);
                                    pickupText.setVisibility(TextView.GONE);
                                    pickupid.setVisibility(TextView.GONE);
=======
>>>>>>> 8df22a4a67e6d2c7632c597099c6c7f051eb41d7
                                }

                            } catch (JSONException e) {
                                Log.e("OrderDetailedActivity", "Error setting order details: " + e.getMessage());
                                Toast.makeText(OrderDetailedActivity.this, "Error loading order details", Toast.LENGTH_SHORT).show();
                            }
                        });

                        List<OrderTableItem> orderItems = new ArrayList<>();

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = items.getJSONObject(i);
                            OrderTableItem orderItem = new OrderTableItem(
                                    item.getString("productName"),
                                    item.getString("size"),
                                    item.getInt("quantity"),
                                    item.getDouble("price"),
                                    item.getString("productImage")
                            );
                            orderItems.add(orderItem);
                        }

                        runOnUiThread(() -> {
                            adapter = new OrderItemAdapter(orderItems);
                            recyclerView.setAdapter(adapter);


                        });
                    }
                } catch (Exception e) {
                    Log.e("OrderDetailedActivity", "Error parsing data: " + e.getMessage());
                    runOnUiThread(() ->
                            Toast.makeText(OrderDetailedActivity.this,
                                    "Error parsing data", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}