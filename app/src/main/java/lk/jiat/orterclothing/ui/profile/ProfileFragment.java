package lk.jiat.orterclothing.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import lk.jiat.orterclothing.EditProfileActivity;
import lk.jiat.orterclothing.LoginActivity;
import lk.jiat.orterclothing.databinding.FragmentCartBinding;
import lk.jiat.orterclothing.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private RecyclerView recyclerView;

    private AddressAdapter addressAdapter;
    FirebaseAuth mAuth;
    FirebaseUser user;

    private List<UserAddress> addressList;
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

        addressList = new ArrayList<>();
        UserAddress address = new UserAddress("Office", "No 1, Colombo 7", "Malabe,70080");
        UserAddress address2 = new UserAddress("Home", "No 26, Wagollawaththa", "Mawathagama,60060");
        addressList.add(address);
        addressList.add(address2);

        addressAdapter = new AddressAdapter(getContext(), addressList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setAdapter(addressAdapter);



        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        TextView name = binding.textView31;
        TextView email = binding.textView42;

        name.setText(user.getDisplayName());

        email.setText(user.getEmail());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}