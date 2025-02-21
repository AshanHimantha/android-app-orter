package lk.jiat.orterclothing;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lk.jiat.orterclothing.databinding.ActivityMainBinding;
import lk.jiat.orterclothing.model.Product;
import lk.jiat.orterclothing.ui.explore.ExploreFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private List<Product> products;
    private ProductAdapter productAdapter;
    private View searchDrawerContent;
    private SearchView searchView;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeViews();
        setupNavigationDrawer();
        setupSearchDrawer();
        setupBottomNavigation();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        searchDrawerContent = findViewById(R.id.search_drawer_content);
        products = new ArrayList<>();
    }

    private void setupBottomNavigation() {

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_cart, R.id.navigation_explore, R.id.orders_nav, R.id.profile_nav)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main2);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    private void setupNavigationDrawer() {
        navView = findViewById(R.id.nav_view);
        NavigationView navigationView = findViewById(R.id.nav_view2);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        TextView menuButton = findViewById(R.id.textView39);
        menuButton.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void setupSearchDrawer() {
        TextView searchButton = findViewById(R.id.textView41);
        recyclerView = searchDrawerContent.findViewById(R.id.search_results_recycler);
        searchView = searchDrawerContent.findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);

        productAdapter = new ProductAdapter(this, products);
        recyclerView.setAdapter(productAdapter);

        searchButton.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.openDrawer(GravityCompat.END);
                loadProducts();
            }
        });

        setupSearchView();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });
    }

    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : products) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        productAdapter.updateList(filteredList);
    }

    private void loadProducts() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://testapi.ashanhimantha.com/api/stock-list")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            products.clear();
                            Gson gson = new Gson();
                            JsonObject jsonObject = gson.fromJson(responseData, JsonObject.class);

                            if (jsonObject.get("status").getAsBoolean()) {
                                JsonArray dataArray = jsonObject.getAsJsonArray("data");

                                for (JsonElement item : dataArray) {
                                    JsonObject productObject = item.getAsJsonObject();

                                    String id = productObject.get("id").getAsString();
                                    String productName = productObject.get("product_name").getAsString();
                                    String imageUrl = productObject.get("main_image").getAsString();
                                    int price = (int) Double.parseDouble(productObject.get("price").getAsString());
                                    String collectionName = productObject.get("collection_name").getAsString();

                                    products.add(new Product(id, productName, imageUrl, price, collectionName));
                                }
                                productAdapter.notifyDataSetChanged();
                            }
                        } catch (JsonSyntaxException e) {
                            Toast.makeText(MainActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.test1) {
            startActivity(new Intent(this, LatestProductActivity.class));
        } else if (id == R.id.test2) {

      NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main2);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navView.setSelectedItemId(R.id.navigation_explore);

     }
//        else if (id == R.id.test3) {
//            startActivity(new Intent(this, SplashActivity.class));
//        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}