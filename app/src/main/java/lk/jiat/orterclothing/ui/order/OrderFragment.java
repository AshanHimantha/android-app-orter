package lk.jiat.orterclothing.ui.order;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;
    import androidx.recyclerview.widget.DividerItemDecoration;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import org.json.JSONArray;
    import org.json.JSONObject;

    import java.io.IOException;
    import java.util.ArrayList;
    import java.util.List;

    import lk.jiat.orterclothing.databinding.FragmentOrderBinding;
    import okhttp3.Call;
    import okhttp3.Callback;
    import okhttp3.MediaType;
    import okhttp3.OkHttpClient;
    import okhttp3.Request;
    import okhttp3.RequestBody;
    import okhttp3.Response;

    public class OrderFragment extends Fragment implements OrderAdapter.OnOrderClickListener {

        private FragmentOrderBinding binding;
        private RecyclerView recyclerView;
        private OrderAdapter orderAdapter;
        private List<Order> orderList;
        private static final String API_URL = "http://10.0.2.2:8000/api/user/orders?firebase_uid=MCKHkk1OgsU0mGz1cbhwkDwvrLl2";

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            binding = FragmentOrderBinding.inflate(inflater, container, false);
            View root = binding.getRoot();

            recyclerView = binding.orderRecyclerView;
            if (recyclerView == null) {
                Log.e("OrderFragment", "RecyclerView is null!");
                return root;
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));



            orderList = new ArrayList<>();
            orderAdapter = new OrderAdapter(getContext(), orderList, this);
            recyclerView.setAdapter(orderAdapter);

            // Debug layout parameters
            ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
            Log.d("OrderFragment", "RecyclerView width: " + params.width + ", height: " + params.height);

            fetchOrders();
            return root;
        }

        private void fetchOrders() {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(API_URL)
                    .get()
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Failed to load orders", Toast.LENGTH_SHORT).show()
                    );
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String responseData = response.body().string();
                    Log.d("OrderFragment", "Response body: " + responseData);

                    if (getActivity() == null) return;

                    getActivity().runOnUiThread(() -> {
                        try {
                            if (response.isSuccessful()) {
                                JSONObject jsonResponse = new JSONObject(responseData);
                                if (jsonResponse.getBoolean("status")) {
                                    JSONArray ordersArray = jsonResponse.getJSONArray("data");
                                    List<Order> orders = new ArrayList<>();

                                    for (int i = 0; i < ordersArray.length(); i++) {
                                        JSONObject orderObj = ordersArray.getJSONObject(i);
                                        JSONArray items = orderObj.getJSONArray("items");
                                        JSONObject firstItem = items.getJSONObject(0);

                                        Order order = new Order(
                                                orderObj.getString("orderID"),
                                                orderObj.getString("orderNumber"),
                                                orderObj.getString("orderDate"),
                                                orderObj.getString("orderStatus"),
                                                orderObj.getString("orderTotal"),
                                                firstItem.getString("productName"),
                                                firstItem.getString("quantity") + " items",
                                                firstItem.getString("productImage")
                                        );
                                        orders.add(order);
                                        Log.d("OrderFragment", "Added order: " + order.getOrderNumber());
                                    }

                                    orderList.clear();
                                    orderList.addAll(orders);
                                    orderAdapter.notifyDataSetChanged();
                                    Log.d("OrderFragment", "Total orders: " + orderList.size());

                                    if (orderList.isEmpty()) {
                                        Toast.makeText(getContext(), "No orders found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("OrderFragment", "Error parsing data: " + e.getMessage());
                            Toast.makeText(getContext(), "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        @Override
        public void onTrackOrderClick(Order order) {
            // Handle track order click
            Toast.makeText(getContext(), "Tracking order: " + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onViewDetailsClick(Order order) {
            // Handle view details click
            Toast.makeText(getContext(), "Viewing details: " + order.getOrderNumber(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            binding = null;
        }
    }