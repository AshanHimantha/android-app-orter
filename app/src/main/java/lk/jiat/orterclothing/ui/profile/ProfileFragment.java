package lk.jiat.orterclothing.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import lk.jiat.orterclothing.AddAddressActivity;
import lk.jiat.orterclothing.EditProfileActivity;
import lk.jiat.orterclothing.LoginActivity;
import lk.jiat.orterclothing.R;
import lk.jiat.orterclothing.DatabaseHelper; // Import the database helper
import lk.jiat.orterclothing.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private List<UserAddress> addressList;
    private DatabaseHelper dbHelper; // Add DatabaseHelper instance

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dbHelper = new DatabaseHelper(context.getApplicationContext());  // Initialize DatabaseHelper here
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.close();

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        recyclerView = binding.recyclerView3;

        Button editProfile = binding.button10;
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        Button signOut = binding.button13;
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });



        Button addAddress = binding.button11;
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddAddressActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        addressList = new ArrayList<>();
        SQLiteDatabase sqdb = dbHelper.getReadableDatabase(); // Get readable database

        // Updated query to include the contact column
        Cursor cursor = sqdb.rawQuery("SELECT display_name, owner_name, address1, address2, zip, contact FROM addresses", null);

        if (cursor.moveToFirst()) {
            do {
                String addressName = cursor.getString(0);
                String owner = cursor.getString(1);
                String addressLine1 = cursor.getString(2);
                String addressLine2 = cursor.getString(3);
                String zip = cursor.getString(4);
                String contact = cursor.getString(5);  // Now safely getting the contact

                addressLine2 = addressLine2 + ", " + zip;
                UserAddress address = new UserAddress(addressName, owner, addressLine1, addressLine2, contact);
                addressList.add(address);
            } while (cursor.moveToNext());
        }

        cursor.close();
        sqdb.close();  // Close the database

        addressAdapter = new AddressAdapter(getContext(), addressList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(addressAdapter);



        TextView name = binding.textView31;
        TextView email = binding.textView42;
        Glide.with(this).load(user.getPhotoUrl()).into(binding.proImage);

        name.setText(user.getDisplayName());

        email.setText(user.getEmail());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").where(Filter.equalTo("uid", user.getUid()))
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        List<DocumentSnapshot> documents = task.getResult().getDocuments();
                        if (documents.size() > 0) {
                            DocumentSnapshot document = documents.get(0);
                            String mobile = document.getString("mobile");
                            binding.textView43.setText(mobile);
                        }
                    }
                });



    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (dbHelper != null) {
            dbHelper.close(); // Close the database helper when the fragment is destroyed
        }
    }
}