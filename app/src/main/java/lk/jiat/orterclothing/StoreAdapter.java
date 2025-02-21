package lk.jiat.orterclothing;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private List<Store> storeList;
    private int selectedPosition = -1;
    private Context activityContext;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    public StoreAdapter(Context context, List<Store> storeList) {
        this.storeList = storeList;
        this.activityContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.store_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Store store = storeList.get(position);
        holder.storeName.setText(store.getStoreName());
        holder.addressLine1.setText(store.getAddress1());
        holder.addressLine2.setText(store.getAddress2() + ", " + store.getZipCode());

        holder.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(activityContext instanceof Activity)) {
                    Log.e("StoreAdapter", "Context is not an instance of Activity");
                    return;
                }
                checkLocationPermission(store);
            }
        });

        if (store.getStoreImage() != null && !store.getStoreImage().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(store.getStoreImage())
                    .placeholder(R.drawable.kandy)
                    .into(holder.storeImage);
        }

        if (position == selectedPosition) {
            holder.itemView.setBackgroundResource(R.drawable.blackborder);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.light_border);
        }

        holder.itemView.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
        });
    }

    private void checkLocationPermission(Store store) {
        if (ContextCompat.checkSelfPermission(activityContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) activityContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showPermissionExplanationDialog(store);
            } else {
                requestLocationPermission(store);
            }
        } else {
            startMapActivity(store);
        }
    }

    private void requestLocationPermission(Store store) {
        ActivityCompat.requestPermissions((Activity) activityContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private void showPermissionExplanationDialog(Store store) {
        new AlertDialog.Builder(activityContext)
                .setTitle("Location Permission Required")
                .setMessage("This app needs location permission to show you the store's location on the map. Please grant the permission.")
                .setPositiveButton("OK", (dialog, which) -> {
                    requestLocationPermission(store);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Store store) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMapActivity(store);
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) activityContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showGoToSettingsDialog();
                } else {
                    showPermissionDeniedMessage();
                }
            }
        }
    }

    private void showGoToSettingsDialog() {
        new AlertDialog.Builder(activityContext)
                .setTitle("Location Permission Required")
                .setMessage("You have previously denied location permission and chosen 'Don't ask again'. Please go to app settings to grant the permission.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activityContext.getPackageName(), null);
                    intent.setData(uri);
                    activityContext.startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showPermissionDeniedMessage() {
        Toast.makeText(activityContext, "Location permission is required to view the store on the map.", Toast.LENGTH_SHORT).show();
    }

    private void startMapActivity(Store store) {
        try {
            Intent intent = new Intent(activityContext, ViewMapActivity.class);
            intent.putExtra("storeName", store.getStoreName());
            intent.putExtra("latitude", Double.parseDouble(store.getLatitude()));
            intent.putExtra("longitude", Double.parseDouble(store.getLongitude()));
            intent.putExtra("contact", store.getStoreContact());
            intent.putExtra("image", store.getStoreImage());
            activityContext.startActivity(intent);
        } catch (NumberFormatException e) {
            Log.e("StoreAdapter", "Error parsing latitude or longitude: " + e.getMessage());
            Toast.makeText(activityContext, "Error: Invalid latitude or longitude data.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public Store getSelectedStore() {
        if (selectedPosition != -1 && selectedPosition < storeList.size()) {
            return storeList.get(selectedPosition);
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView storeImage;
        TextView storeName;
        TextView addressLine1;
        TextView addressLine2;
        Button location;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storeImage = itemView.findViewById(R.id.proImage);
            storeName = itemView.findViewById(R.id.textView59);
            addressLine1 = itemView.findViewById(R.id.textView60);
            addressLine2 = itemView.findViewById(R.id.textView61);
            location = itemView.findViewById(R.id.button15);
        }
    }
}