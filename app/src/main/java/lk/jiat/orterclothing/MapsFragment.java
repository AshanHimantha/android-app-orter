package lk.jiat.orterclothing;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapsFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean locationPermissionGranted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map); //Use getChildFragmentManager()
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "Error: SupportMapFragment not found.");
        }


        getLocationPermission();  // Request location permission

        // Check if location permission is already granted
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        }
    }

   
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Check for location permission again, as it might have changed since onViewCreated
        if (locationPermissionGranted) {
            enableMyLocation();
            getDeviceLocation();
        } else {
            // Location permission is not granted, request it again.
            getLocationPermission();
            // Optionally, handle the case where permission is permanently denied
            Toast.makeText(getContext(), "Location permission is required to show your location on the map.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
                // Permission granted, you can now enable location
                enableMyLocation();
                getDeviceLocation();
            } else {
                locationPermissionGranted = false;
                // Permission denied, handle the situation
                Toast.makeText(getContext(), "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableMyLocation() {
        if (mMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        if (locationPermissionGranted) {

            locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // New location found
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latLng = new LatLng(latitude, longitude);

                    // Move the camera to the new location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                    // Add a marker
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));

                    //Remove the location listener after first location. Optional.
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {
                    Toast.makeText(getContext(), "Please enable location services!", Toast.LENGTH_SHORT).show();
                }
            };

            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
            } catch (SecurityException e) {
                Log.e("Exception: %s", e.getMessage());
            }
        } else {
            getLocationPermission();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Important: Remove the location listener when the fragment is destroyed
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}