package lk.jiat.orterclothing;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lk.jiat.orterclothing.ui.profile.AddressAdapter;
import lk.jiat.orterclothing.ui.profile.UserAddress;
import lk.payhere.androidsdk.PHConfigs;
import lk.payhere.androidsdk.PHConstants;
import lk.payhere.androidsdk.PHMainActivity;
import lk.payhere.androidsdk.PHResponse;
import lk.payhere.androidsdk.model.InitRequest;
import lk.payhere.androidsdk.model.Item;
import lk.payhere.androidsdk.model.StatusResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CheckoutActivity extends AppCompatActivity {
    private static final String API_BASE_URL = "http://10.0.2.2:8000/api";
    private static final int PAYHERE_REQUEST = 11001;
    private final OkHttpClient client = new OkHttpClient();
    private final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    RecyclerView recyclerView;
    RecyclerView storeRecyclerView;
    StoreAdapter storeAdapter;
    private List<UserAddress> addressList;
    private AddressAdapter addressAdapter;
    private DatabaseHelper dbHelper;
    private TextView total;

    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        setupWindowInsets();
        initializeViews();
        setupIntentData();
        setupBackButton();
        setupAddressList();
        setupStoreList();
        setupChipGroup();
        setupConfirmButton();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });
    }

    private void initializeViews() {
        total = findViewById(R.id.textView20);
        recyclerView = findViewById(R.id.addressCheckout);
        storeRecyclerView = findViewById(R.id.store3);
        dbHelper = new DatabaseHelper(this);
    }

    private void setupIntentData() {
        Intent intent = getIntent();
        String totalAmount = intent.getStringExtra("total");
        String items = intent.getStringExtra("itemCount");
        String shipping = intent.getStringExtra("shipping");
        String subTotal = intent.getStringExtra("subtotal");

        ((TextView) findViewById(R.id.textView20)).setText(totalAmount);
        ((TextView) findViewById(R.id.textView18)).setText(items);
        ((TextView) findViewById(R.id.textView30)).setText(shipping);
        ((TextView) findViewById(R.id.textView15)).setText(subTotal);

        findViewById(R.id.storeLayout).setVisibility(View.GONE);
    }

    private void setupBackButton() {
        findViewById(R.id.button14).setOnClickListener(v -> finish());
    }

    private void setupAddressList() {
        addressList = new ArrayList<>();
        addressAdapter = new AddressAdapter(this, addressList);
        recyclerView.setAdapter(addressAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        findViewById(R.id.textView51).setOnClickListener(v ->
                startActivity(new Intent(CheckoutActivity.this, AddAddressActivity.class))
        );
    }

    private void setupStoreList() {
        List<Store> storeList = new ArrayList<>();
        storeAdapter = new StoreAdapter(storeList);
        storeRecyclerView.setAdapter(storeAdapter);
        storeRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        FirebaseFirestore.getInstance().collection("store").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                task.getResult().getDocuments().forEach(document -> {
                    Store store = new Store(
                            document.getString("name"),
                            document.getString("contact"),
                            document.getString("image"),
                            document.getString("address1"),
                            document.getString("address2"),
                            document.getString("zip"),
                            document.getString("latitude"),
                            document.getString("longitude")
                    );
                    storeList.add(store);
                });
                storeAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupChipGroup() {
        ChipGroup chipGroup = findViewById(R.id.chipGroup);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            updateChipColors(group, checkedId);

            // Check if store pickup is selected (chip3)
            if (checkedId == R.id.chip3) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("user")
                        .document(currentUser.getUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().exists() && task.getResult().get("mobile") != null) {
                                    // User has mobile number, show store layout
                                    updateLayoutVisibility(checkedId);
                                } else {
                                    // No mobile number found
                                    runOnUiThread(() -> {
                                        Toast.makeText(CheckoutActivity.this,
                                                "Please add your mobile number in profile first",
                                                Toast.LENGTH_LONG).show();
                                        // Reset chip selection
                                        chipGroup.clearCheck();
                                        // Optionally navigate to profile
                                        Intent intent = new Intent(CheckoutActivity.this, EditProfileActivity.class);
                                        startActivity(intent);
                                    });
                                }
                            } else {
                                // Error checking mobile
                                runOnUiThread(() -> {
                                    Toast.makeText(CheckoutActivity.this,
                                            "Error checking profile information",
                                            Toast.LENGTH_SHORT).show();
                                    chipGroup.clearCheck();
                                });
                            }
                        });
            } else {
                // For other chip selections, just update layout
                updateLayoutVisibility(checkedId);
            }
        });
    }

    private void updateChipColors(ChipGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            Chip chip = (Chip) group.getChildAt(i);
            chip.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }
        if (checkedId != View.NO_ID) {
            Chip selectedChip = group.findViewById(checkedId);
            if (selectedChip != null) {
                selectedChip.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            }
        }
    }

    private void updateLayoutVisibility(int checkedId) {
        findViewById(R.id.constraintLayout2).setVisibility(
                checkedId == R.id.chip2 ? View.VISIBLE : View.GONE
        );
        findViewById(R.id.storeLayout).setVisibility(
                checkedId == R.id.chip3 ? View.VISIBLE : View.GONE
        );
    }

    private void setupConfirmButton() {
        findViewById(R.id.button4).setOnClickListener(v -> {
            ChipGroup chipGroup = findViewById(R.id.chipGroup);
            int selectedChipId = chipGroup.getCheckedChipId();

            if (selectedChipId == View.NO_ID) {
                Toast.makeText(this, "Please select Delivery or Store pickup", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.getIdToken(true).addOnSuccessListener(result -> {
                String token = "Bearer " + result.getToken();
                createOrder(token, selectedChipId);
            });
        });
    }

    private void createOrder(String token, int selectedChipId) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("payment_method", "payhere");

            if (selectedChipId == R.id.chip2) {
                if (addressAdapter.getSelectedPosition() == -1) {
                    Toast.makeText(this, "Please select a delivery address", Toast.LENGTH_SHORT).show();
                    return;
                }
                addDeliveryDetails(requestBody);
            } else {
                if (storeAdapter.getSelectedStore() == null) {
                    Toast.makeText(this, "Please select a store", Toast.LENGTH_SHORT).show();
                    return;
                }
                addPickupDetails(requestBody);
            }

            sendOrderRequest(token, requestBody);
        } catch (JSONException e) {
            Toast.makeText(this, "Error creating request", Toast.LENGTH_SHORT).show();
        }
    }

    private void addDeliveryDetails(JSONObject requestBody) throws JSONException {
        UserAddress selectedAddress = addressList.get(addressAdapter.getSelectedPosition());
        requestBody.put("delivery_type", "delivery");
        requestBody.put("delivery_name", selectedAddress.getFName() + " " + selectedAddress.getLName());
        requestBody.put("delivery_phone", selectedAddress.getContact());
        requestBody.put("delivery_address", selectedAddress.getAddressLine1());
        requestBody.put("delivery_city", selectedAddress.getAddressLine2());
        requestBody.put("firebase_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        requestBody.put("payment_method", "card");
    }

    private void addPickupDetails(JSONObject requestBody) throws JSONException {
        Store selectedStore = storeAdapter.getSelectedStore();
        requestBody.put("delivery_type", "pickup");
        requestBody.put("branch_name", selectedStore.getStoreName());
        requestBody.put("delivery_name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        requestBody.put("delivery_phone", "0779678082");
        requestBody.put("firebase_uid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        requestBody.put("payment_method", "card");
        requestBody.put("delivery_address","Store pickup");
        requestBody.put("delivery_city", "Store pickup");
    }

    private void sendOrderRequest(String token, JSONObject requestBody) {
        RequestBody body = RequestBody.create(requestBody.toString(), JSON);
        Request request = new Request.Builder()
                .url(API_BASE_URL + "/orders")
                .addHeader("Authorization", token)
                .addHeader("Accept", "application/json")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(() ->
                        Toast.makeText(CheckoutActivity.this, "Network error", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body() != null ? response.body().string() : "No response body";


                if (response.isSuccessful()) {
                    try {
                        JSONObject orderResponse = new JSONObject(responseBody);


                        // Log specific order details
                        if (orderResponse.has("data")) {
                            JSONObject data = orderResponse.getJSONObject("data");
                            if (data.has("order")) {
                                JSONObject order = data.getJSONObject("order");

                            }
                        }

                        runOnUiThread(() -> initPayHerePayment(orderResponse));
                    } catch (JSONException e) {
                        runOnUiThread(() ->
                                Toast.makeText(CheckoutActivity.this, "Error processing response", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {

Log.e("CheckoutActivity", "Failed to create order: " + responseBody);
                    runOnUiThread(() ->
                            Toast.makeText(CheckoutActivity.this, "Failed to create order", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void initPayHerePayment(JSONObject orderDetails) {
        try {
            JSONObject data = orderDetails.getJSONObject("data");
            JSONObject order = data.getJSONObject("order");
            JSONObject summary = data.getJSONObject("summary");

            orderId = order.getString("id");

            InitRequest req = new InitRequest();
            req.setMerchantId("1221046");
            req.setCurrency("LKR");
            req.setAmount(summary.getDouble("total"));
            req.setOrderId(order.getString("order_number"));
            req.setItemsDescription("Payment for Order " + order.getString("order_number"));
            req.setCustom1(order.getString("delivery_type"));
            req.setCustom2(order.getString("payment_method"));
            String[] names = order.getString("delivery_name").split(" ", 2);
            req.getCustomer().setFirstName(names[0]);
            req.getCustomer().setLastName(names.length > 1 ? names[1] : "");
            req.getCustomer().setEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
            req.getCustomer().setPhone(order.getString("delivery_phone"));
            req.getCustomer().getAddress().setAddress(order.getString("delivery_address"));
            req.getCustomer().getAddress().setCity(order.getString("delivery_city"));
            req.getCustomer().getAddress().setCountry("Sri Lanka");
            req.getCustomer().getDeliveryAddress().setAddress(order.getString("delivery_address"));
            req.getCustomer().getDeliveryAddress().setCity(order.getString("delivery_city"));
            req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");
            req.setNotifyUrl("https://testapi.ashanhimantha.com/api/payhere/notify");

            // Add items
            JSONArray items = order.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                req.getItems().add(new Item(
                        null,
                        item.getString("product_name"),
                        item.getInt("quantity"),
                        Double.parseDouble(item.getString("selling_price"))
                ));
            }

            Intent intent = new Intent(this, PHMainActivity.class);
            intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);
            PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
            startActivityForResult(intent, PAYHERE_REQUEST);

            Log.d("CheckoutActivity", "Payment initialization request: " + req.toString());

        } catch (JSONException e) {
            Log.e("CheckoutActivity", "Error initializing payment: " + e.getMessage());
            Toast.makeText(this, "Error initializing payment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);

            Log.d("CheckoutActivityPay", "Payment response: " + response.toString());


            handlePaymentResponse(resultCode, response);
        }
    }

    private void handlePaymentResponse(int resultCode, PHResponse<StatusResponse> response) {
        if (resultCode == Activity.RESULT_OK) {
            if (response != null && response.isSuccess()) {
                Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else {
                sendPaymentStatusUpdate();
                Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            sendPaymentStatusUpdate();
            Log.e("payment", orderId);
            Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
        }
    }


private void sendPaymentStatusUpdate() {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String token = task.getResult().getToken();

                    Request request = new Request.Builder()
                        .url(API_BASE_URL + "/orders/" + orderId)
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Bearer " + token)
                        .delete()
                        .build();

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(() -> Toast.makeText(CheckoutActivity.this,
                                    "Failed to cancel order", Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                runOnUiThread(() -> Toast.makeText(CheckoutActivity.this,
                                        "Error cancelling order", Toast.LENGTH_SHORT).show());
                            }
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(CheckoutActivity.this,
                            "Failed to get authentication token", Toast.LENGTH_SHORT).show());
                }
            });
        }



    @Override
    protected void onResume() {
        super.onResume();
        loadAddressList();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadAddressList() {
        addressList.clear();
        SQLiteDatabase sqdb = dbHelper.getReadableDatabase();
        Cursor cursor = sqdb.rawQuery("SELECT display_name, f_name, l_name, address1, address2, zip, contact FROM addresses", null);

        if (cursor.moveToFirst()) {
            do {
                UserAddress address = new UserAddress(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4) + ", " + cursor.getString(5),
                        cursor.getString(6)
                );
                addressList.add(address);
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqdb.close();
        addressAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}