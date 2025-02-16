package lk.jiat.orterclothing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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



public class CheckoutActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private List<UserAddress> addressList;
    private AddressAdapter addressAdapter;
    private DatabaseHelper dbHelper; // Add DatabaseHelper instance
    private static final int PAYHERE_REQUEST = 11001;
    private static final String TAG = "CheckoutActivity";
    private TextView resultTextView;  // Declare TextView for result display

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        String total = intent.getStringExtra("total");
        String items = intent.getStringExtra("itemCount");
        String shipping = intent.getStringExtra("shipping");
        String subTotal = intent.getStringExtra("subtotal");

        TextView totalView = findViewById(R.id.textView20);
        TextView itemCount = findViewById(R.id.textView18);
        TextView shippingView = findViewById(R.id.textView30);
        TextView subTotalView = findViewById(R.id.textView15);

        totalView.setText(total);
        itemCount.setText(items);
        shippingView.setText(shipping);
        subTotalView.setText(subTotal);



        Button back = findViewById(R.id.button14);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button confirm = findViewById(R.id.button4);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ChipGroup chipGroup = findViewById(R.id.chipGroup);
                int selectedChipId = chipGroup.getCheckedChipId();

                if (selectedChipId == View.NO_ID) {
                    Toast.makeText(CheckoutActivity.this, "Please select Delivery or Store pickup", Toast.LENGTH_SHORT).show();
                    return;
                }

                Chip selectedChip = findViewById(selectedChipId);
                String deliveryMethod = selectedChip.getText().toString();

                if (selectedChipId == R.id.chip2) {

                    if (addressAdapter.getSelectedPosition() == -1) {
                        Toast.makeText(CheckoutActivity.this, "Please select a delivery address", Toast.LENGTH_SHORT).show();
                        return;
                    } else {




                        Log.d("Selected Address", addressList.get(addressAdapter.getSelectedPosition()).getAddressName());
                        InitRequest req = new InitRequest();
                        req.setMerchantId("1221046");       // Merchant ID
                        req.setCurrency("LKR");             // Currency code LKR/USD/GBP/EUR/AUD
                        req.setAmount(1000.00);             // Final Amount to be charged
                        req.setOrderId("230000123");        // Unique Reference ID
                        req.setItemsDescription("Door bell wireless");  // Item description title
                        req.setCustom1("This is the custom message 1");
                        req.setCustom2("This is the custom message 2");
                        req.getCustomer().setFirstName("Saman");
                        req.getCustomer().setLastName("Perera");
                        req.getCustomer().setEmail("samanp@gmail.com");
                        req.getCustomer().setPhone("+94771234567");
                        req.getCustomer().getAddress().setAddress("No.1, Galle Road");
                        req.getCustomer().getAddress().setCity("Colombo");
                        req.getCustomer().getAddress().setCountry("Sri Lanka");

                        // Optional Params
                        // req.setNotifyUrl("xxxx");           // Notify Url
                        req.getCustomer().getDeliveryAddress().setAddress("No.2, Kandy Road");
                        req.getCustomer().getDeliveryAddress().setCity("Kadawatha");
                        req.getCustomer().getDeliveryAddress().setCountry("Sri Lanka");
                        req.getItems().add(new Item(null, "Door bell wireless", 1, 1000.0));

                        Intent intent = new Intent(CheckoutActivity.this, PHMainActivity.class);
                        intent.putExtra(PHConstants.INTENT_EXTRA_DATA, req);

                        PHConfigs.setBaseUrl(PHConfigs.SANDBOX_URL);
                        startActivityForResult(intent, PAYHERE_REQUEST);


                    }
                }else if (selectedChipId == R.id.chip3) {
                    Log.d("Selected Address", "Store Pickup");
                }else {
                    Toast.makeText(CheckoutActivity.this, "Please select Delivery or Store pickup", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });





        dbHelper = new DatabaseHelper(this); // Initialize the DatabaseHelper


        TextView addAddress = findViewById(R.id.textView51);
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckoutActivity.this, AddAddressActivity.class);
                startActivity(intent);
            }
        });

        addressList = new ArrayList<>();
        addressAdapter = new AddressAdapter(this, addressList);
        recyclerView = findViewById(R.id.addressCheckout);
        recyclerView.setAdapter(addressAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        MapsFragment mapsFragment = new MapsFragment();
//        findViewById(R.id.fragmentContainerView2).setVisibility(View.GONE);

        ChipGroup chipGroup = findViewById(R.id.chipGroup);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < group.getChildCount(); i++) {
                Chip chip = (Chip) group.getChildAt(i);
                chip.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            }
            if (checkedId == R.id.chip2) {
                findViewById(R.id.constraintLayout2).setVisibility(View.VISIBLE);
//        findViewById(R.id.fragmentContainerView2).setVisibility(View.GONE);
            } else if (checkedId == R.id.chip3) {
                findViewById(R.id.constraintLayout2).setVisibility(View.GONE);
//        findViewById(R.id.fragmentContainerView2).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.constraintLayout2).setVisibility(View.GONE);
//        findViewById(R.id.fragmentContainerView2).setVisibility(View.GONE);
            }
            if (checkedId != View.NO_ID) {
                Chip selectedChip = group.findViewById(checkedId);
                if (selectedChip != null) {
                    runOnUiThread(() -> {
                        selectedChip.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                        selectedChip.invalidate();
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddressList();
    }

    private void loadAddressList() {
        addressList.clear();
        SQLiteDatabase sqdb = dbHelper.getReadableDatabase();
        Cursor cursor = sqdb.rawQuery("SELECT display_name, owner_name, address1, address2, zip, contact FROM addresses", null);

        if (cursor.moveToFirst()) {
            do {
                String addressName = cursor.getString(0);
                String owner = cursor.getString(1);
                String addressLine1 = cursor.getString(2);
                String addressLine2 = cursor.getString(3);
                String zip = cursor.getString(4);
                String contact = cursor.getString(5);

                addressLine2 = addressLine2 + ", " + zip;
                UserAddress address = new UserAddress(addressName, owner, addressLine1, addressLine2, contact);
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
            dbHelper.close();  // Close the database helper
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYHERE_REQUEST && data != null && data.hasExtra(PHConstants.INTENT_EXTRA_RESULT)) {
            PHResponse<StatusResponse> response = (PHResponse<StatusResponse>) data.getSerializableExtra(PHConstants.INTENT_EXTRA_RESULT);
            if (resultCode == Activity.RESULT_OK) {
                String msg;
                if (response != null) {
                    if (response.isSuccess()) {
                        msg = "Payment Success: " + response.getData().toString();
                    } else {
                        msg = "Payment Failed: " + response.toString();
                    }
                } else {
                    msg = "Payment Result: No response from PayHere";
                }
                Log.d(TAG, msg);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (response != null) {
                    resultTextView.setText("Payment Canceled: " + response.toString());
                } else {
                    resultTextView.setText("Payment Canceled: User canceled the request");
                }
            }
        }
    }
}