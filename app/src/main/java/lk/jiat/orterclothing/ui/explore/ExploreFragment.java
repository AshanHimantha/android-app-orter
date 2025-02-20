package lk.jiat.orterclothing.ui.explore;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lk.jiat.orterclothing.ProductAdapter;
import lk.jiat.orterclothing.R;
import lk.jiat.orterclothing.databinding.FragmentExploreBinding;
import lk.jiat.orterclothing.model.Product;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ExploreFragment extends Fragment {

    RecyclerView recyclerView;
    private FragmentExploreBinding binding;
    private ProductAdapter productAdapter;

    private List<Product> productList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        recyclerView = root.findViewById(R.id.exploreRecycler);
        binding.exploreRecycler.setLayoutManager(new GridLayoutManager(getContext(),2));

        productAdapter = new ProductAdapter(getContext(), productList);
        binding.exploreRecycler.setAdapter(productAdapter);
        loadProducts();

        return root;
    }


    private void loadProducts() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://testapi.ashanhimantha.com/api/stock-list")
                .build();

       client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("API", "Failed to fetch data");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(myResponse);
                        JSONArray jsonArray = jsonResponse.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Product product = new Product(
                                    String.valueOf(jsonObject.getInt("id")),
                                    jsonObject.getString("product_name"),
                                    jsonObject.getString("main_image"),
                                    jsonObject.getDouble("price"),
                                    jsonObject.getString("collection_name")
                            );
                            productList.add(product);
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                productAdapter.setProductList(productList);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
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