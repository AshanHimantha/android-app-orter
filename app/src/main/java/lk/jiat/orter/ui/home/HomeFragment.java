package lk.jiat.orter.ui.home;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lk.jiat.orter.R;
import lk.jiat.orter.databinding.FragmentHomeBinding;
import lk.jiat.orter.model.Product;
import lk.jiat.orter.ProductAdapter; // Import the adapter

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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

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
        productList.add(new Product("Oversized Hoodie", "https://orterclothing.com/assets/div8.jpg", 3999, "Techno Demon"));
        productList.add(new Product("Minimalist T-Shirt", "https://orterclothing.com/assets/div10.jpg", 1999, "Techno Demon"));
        productList.add(new Product("Urban Joggers", "https://orterclothing.com/assets/div8.jpg", 2999, "Techno Demon"));
        productList.add(new Product("Techno Demon Tee", "https://orterclothing.com/assets/div10.jpg", 2599, "Techno Demon"));
        productList.add(new Product("Oversized Hoodie", "https://orterclothing.com/assets/div8.jpg", 3999, "Techno Demon"));
        productList.add(new Product("Minimalist T-Shirt", "https://orterclothing.com/assets/div10.jpg", 1999, "Techno Demon"));
        productList.add(new Product("Urban Joggers", "https://orterclothing.com/assets/div8.jpg", 2999, "Techno Demon"));
        productList.add(new Product("Techno Demon Tee", "https://orterclothing.com/assets/div10.jpg", 2599, "Techno Demon"));
        productList.add(new Product("Oversized Hoodie", "https://orterclothing.com/assets/div8.jpg", 3999, "Techno Demon"));
        productList.add(new Product("Minimalist T-Shirt", "https://orterclothing.com/assets/div10.jpg", 1999, "Techno Demon"));
        productList.add(new Product("Urban Joggers", "https://orterclothing.com/assets/div8.jpg", 2999, "Techno Demon"));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
        binding = null;
    }
}