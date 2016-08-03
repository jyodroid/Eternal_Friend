package com.jyo.android.eternalfriend;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jyo.android.eternalfriend.network.NetworkHelper;
import com.jyo.android.eternalfriend.places.PlacesConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.util.concurrent.ExecutionException;


public class MapsActivity extends FragmentActivity
        implements
        OnMapReadyCallback {

    private static final String RESULTS_ATR_NAME = "results";
    private LatLng mYourPosition;
    private Location mYourLocation;

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Context mContext;
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            NetworkHelper networkHelper = NetworkHelper.getInstance(mContext);
            JsonObjectRequest request =
                    PlacesConnection.createPlacesRequest(
                            mContext, mOnSuccessHandler, mOnErrorHandler, mYourPosition);
            try {
                networkHelper.addToRequestQueue(request);
            } catch (ConnectException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    Response.Listener<JSONObject> mOnSuccessHandler = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            try {

                JSONArray points = response.getJSONArray(RESULTS_ATR_NAME);

                if (points != null && points.length() > 0) {
                    for (int i = 0; i < points.length(); i++) {
                        final JSONObject object = (JSONObject) points.get(i);
                        JSONObject geometry = (JSONObject) object.get("geometry");
                        JSONObject location = (JSONObject) geometry.get("location");
                        double lat = Double.valueOf(location.get("lat").toString());
                        double lng = Double.valueOf(location.get("lng").toString());
                        boolean open = false;
                        if (object.has("opening_hours")) {
                            JSONObject openingHours = (JSONObject) object.get("opening_hours");
                            open = openingHours.getBoolean("open_now");
                        }
                        String strOpen = open ? "open" : "closed";

                        LatLng position = new LatLng(lat, lng);
                        final MarkerOptions markerOptions =
                                new MarkerOptions()
                                        .position(position)
                                        .title(object.getString("name"))
                                        .snippet(strOpen);
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pet_shop));
                        mMap.addMarker(markerOptions);

                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mYourPosition));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    Response.ErrorListener mOnErrorHandler = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            VolleyLog.e("Error", error);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                1, mLocationListener);

        double latitude = 0;
        double longitude = 0;
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }

            mYourLocation = mLocationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (mYourLocation != null) {
                latitude = mYourLocation.getLatitude();
                longitude = mYourLocation.getLongitude();
            }
        }

        mYourPosition = new LatLng(latitude, longitude);
        mContext = this;
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(
                new MarkerOptions()
                        .position(mYourPosition)
                        .title("your position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mYourPosition));
    }
}
