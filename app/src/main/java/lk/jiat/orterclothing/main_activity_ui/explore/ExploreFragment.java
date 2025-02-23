package lk.jiat.orterclothing.main_activity_ui.explore;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lk.jiat.orterclothing.R;
import lk.jiat.orterclothing.adpters.ProductAdapter;
import lk.jiat.orterclothing.databinding.FragmentExploreBinding;
import lk.jiat.orterclothing.model.Category;
import lk.jiat.orterclothing.model.Product;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExploreFragment extends Fragment {

    private RecyclerView recyclerView;
    private FragmentExploreBinding binding;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();
    private List<Product> originalProductList = new ArrayList<>();

    @SuppressLint("ResourceType")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize RecyclerView
        recyclerView = root.findViewById(R.id.exploreRecycler);
        binding.exploreRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));

        productAdapter = new ProductAdapter(getContext(), productList);
        binding.exploreRecycler.setAdapter(productAdapter);

        // Load products from API
        loadProducts();

        // Initialize categories
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category(R.drawable.tshirt, "All", "Unisex T-Shirts Collection", "0"));
        categoryList.add(new Category(R.drawable.tshirt, "T-Shirts", "Unisex T-Shirts Collection", "16"));
        categoryList.add(new Category(R.drawable.shirt, "Shirts", "Men's Shirts Collection", "2"));
        categoryList.add(new Category(R.drawable.trousers, "Pants", "Men's Pants Collection", "3"));
        categoryList.add(new Category(R.drawable.dress, "Dresses", "Women's Dresses Collection", "6"));
        categoryList.add(new Category(R.drawable.womensshirt, "Tops", "Women's Tops Collection", "7"));
        categoryList.add(new Category(R.drawable.skirt, "Skirts", "Women's Skirts Collection", "8"));
        categoryList.add(new Category(R.drawable.hoodie, "Hoodies", "Unisex Hoodies Collection", "11"));
        categoryList.add(new Category(R.drawable.coat, "Jackets", "Unisex Jackets Collection", "12"));
        categoryList.add(new Category(R.drawable.bag, "Bags", "Unisex Bags Collection", "14"));
        categoryList.add(new Category(R.drawable.bracelet, "Other", "Unisex Accessories Collection", "15"));

        // Set up ChipGroup listener
        ChipGroup chipGroup = root.findViewById(R.id.chipgroup2);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != View.NO_ID) {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    String categoryName = chip.getText().toString();
                    filterProducts(categoryName);
                }
            }
        });

        return root;
    }

    private void filterProducts(String categoryName) {
        if (productList.isEmpty()) {
            return;
        }

        List<Product> filteredList = new ArrayList<>();

        if (categoryName.equals("All")) {
            filteredList.addAll(originalProductList);
        } else {
            for (Product product : originalProductList) {
                if (product.getCollection().toLowerCase().equals(categoryName.toLowerCase())) {
                    filteredList.add(product);
                }
            }
        }

        productList.clear();
        productList.addAll(filteredList);
        productAdapter.notifyDataSetChanged();
    }

    private void loadProducts() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://testapi.ashanhimantha.com/api/stock-list")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("API", "Failed to fetch data", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String myResponse = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(myResponse);
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");

                        productList.clear();
                        originalProductList.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Product product = new Product(
                                    String.valueOf(jsonObject.getInt("id")),
                                    jsonObject.getString("product_name"),
                                    jsonObject.getString("main_image"),
                                    jsonObject.getDouble("price"),
                                    jsonObject.getString("collection_name"),
                                    jsonObject.getString("category_id")
                            );
                            productList.add(product);
                            originalProductList.add(product);
                        }

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                productAdapter.notifyDataSetChanged();
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("API", "JSON parsing error", e);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}