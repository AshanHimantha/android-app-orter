package lk.jiat.orterclothing;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import lk.jiat.orterclothing.ui.profile.AddressAdapter;
import lk.jiat.orterclothing.ui.profile.UserAddress;

public class CheckoutActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private List<UserAddress> addressList;
    private AddressAdapter addressAdapter;

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
        SQLiteDatabase sqdb = openOrCreateDatabase("address.db", Context.MODE_PRIVATE, null);
        Cursor cursor = sqdb.rawQuery("SELECT display_name, owner_name, address1, address2, zip FROM addresses", null);

        if (cursor.moveToFirst()) {
            do {
                String addressName = cursor.getString(0);
                String owner = cursor.getString(1);
                String addressLine1 = cursor.getString(2);
                String addressLine2 = cursor.getString(3);
                String zip = cursor.getString(4);

                addressLine2 = addressLine2 + ", " + zip;
                UserAddress address = new UserAddress(addressName, owner, addressLine1, addressLine2);
                addressList.add(address);
            } while (cursor.moveToNext());
        }

        cursor.close();
        addressAdapter.notifyDataSetChanged();
    }
}