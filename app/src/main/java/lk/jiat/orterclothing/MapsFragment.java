package lk.jiat.orterclothing;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapsFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 123;
    private static final int LOCATION_UPDATE_INTERVAL = 5000;
    private static final float MIN_DISTANCE = 10;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean locationPermissionGranted = false;
    private Location currentLocation;

    private String storeName;
    private Double latitude;
    private Double longitude;
    private String image;

    private String contact;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            storeName = bundle.getString("storeName");
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
            image = bundle.getString("image");



        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }



        getLocationPermission();
    }





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (locationPermissionGranted) {
            enableMyLocation();
            getDeviceLocation();
        } else {
            getLocationPermission();
            Toast.makeText(getContext(),
                "Location permission is required to show your location on the map.",
                Toast.LENGTH_SHORT).show();
        }
    }

private void getLocationPermission() {
    if (ContextCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        locationPermissionGranted = true;
    } else {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            locationPermissionGranted = grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (locationPermissionGranted) {
                enableMyLocation();
                getDeviceLocation();
            } else {
                Toast.makeText(getContext(), "Location permission denied.",
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void enableMyLocation() {
        if (mMap == null) return;
        try {
            if (locationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
    }

    private void getDeviceLocation() {
        if (!locationPermissionGranted) return;

        locationManager = (LocationManager) requireContext()
            .getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
                LatLng currentLatLng = new LatLng(location.getLatitude(),
                    location.getLongitude());
                LatLng destinationLatLng = new LatLng(latitude, longitude);

                mMap.clear();

                // Add current location marker
                mMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .title("Your Location")
                        .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                // Add destination marker
                mMap.addMarker(new MarkerOptions()
                        .position(destinationLatLng)
                        .title(storeName));



                // Draw navigation path
                drawPath(currentLatLng, destinationLatLng);

                // Show both markers in view
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(currentLatLng);
                builder.include(destinationLatLng);
                LatLngBounds bounds = builder.build();
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(getContext(), "Please enable location services!",
                    Toast.LENGTH_SHORT).show();
            }
        };

        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_UPDATE_INTERVAL,
                MIN_DISTANCE,
                locationListener
            );
        } catch (SecurityException e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
    }

    private void drawPath(LatLng origin, LatLng destination) {
        String url = getDirectionsUrl(origin, destination);
        new FetchDirectionsTask().execute(url);
    }

    private String getDirectionsUrl(LatLng origin, LatLng destination) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + destination.latitude + ","
            + destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        return "https://maps.googleapis.com/maps/api/directions/json?"
            + parameters + "&key=" + getString(R.string.google_maps_key);
    }

    private class FetchDirectionsTask extends
            AsyncTask<String, Void, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                JSONObject jObject = new JSONObject(data);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                return parser.parse(jObject);
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            if (result == null) return;

            for (List<HashMap<String, String>> path : result) {
                ArrayList<LatLng> points = new ArrayList<>();
                PolylineOptions lineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path) {
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    points.add(new LatLng(lat, lng));
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);

                if (mMap != null) {
                    mMap.addPolyline(lineOptions);
                }
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        HttpURLConnection urlConnection = null;
        StringBuilder data = new StringBuilder();

        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            try (InputStream iStream = urlConnection.getInputStream();
                 BufferedReader br = new BufferedReader(
                     new InputStreamReader(iStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    data.append(line);
                }
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
    }
}