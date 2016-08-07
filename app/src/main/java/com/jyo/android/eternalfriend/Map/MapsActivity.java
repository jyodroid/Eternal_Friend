package com.jyo.android.eternalfriend.Map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jyo.android.eternalfriend.R;
import com.jyo.android.eternalfriend.commons.NetworkHelper;
import com.jyo.android.eternalfriend.commons.PermissionsHelper;
import com.jyo.android.eternalfriend.places.PlacesConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;


public class MapsActivity extends AppCompatActivity
        implements
        OnMapReadyCallback {

    private static final String LOG_TAG = MapsActivity.class.getSimpleName();

    private static final String RESULTS_ATR_NAME = "results";
    private LatLng mYourPosition;
    private ProgressBar mProgressBar;

    private GoogleMap mMap;
    SupportMapFragment mMapFragment;
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
                        double lat = location.getDouble("lat");
                        double lng = location.getDouble("lng");
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
                        JSONArray types = object.getJSONArray("types");
                        int resource = R.drawable.ic_pet_shop;
                        for (int j = 0; j < types.length(); j++) {
                            String type = (String) types.get(j);
                            if ("veterinary_care".equals(type)) {
                                resource = R.drawable.ic_veterinarian;
                                break;
                            }
                        }
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(resource));
                        mMap.addMarker(markerOptions);

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mProgressBar.setVisibility(View.GONE);
        }
    };

    Response.ErrorListener mOnErrorHandler = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            VolleyLog.e("Error", error);
            mProgressBar.setVisibility(View.GONE);
        }
    };

    private boolean isAskingForPermission = false;
    private PermissionsHelper.HandleMessage handleMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mProgressBar = (ProgressBar) findViewById(R.id.map_progress_bar);
        mMapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null){
            mMap = googleMap;
        }

        if (handleMessage == null) {
            handleMessage = new PermissionsHelper.HandleMessage();
        }
        isAskingForPermission = handleMessage.isAskingForPermission();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !isAskingForPermission) {
            //Obtain permission type required
            int requiredPermissionType = PermissionsHelper.obtainPermissionType(this);
            if (requiredPermissionType != PermissionsHelper.MY_PERMISSIONS_UNKNOWN) {
                //Prepare Thread to show message and handler to management
                handleMessage.isAskingForPermission(true);
                PermissionsHelper.requestFeaturePermissions(
                        requiredPermissionType, this, handleMessage);
            } else {
                setYourLocation();
            }
        } else if (!isAskingForPermission) {
            setYourLocation();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return (super.onOptionsItemSelected(item));
    }

    private void setYourLocation() {
        mProgressBar.setVisibility(View.VISIBLE);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        double latitude = 0;
        double longitude = 0;
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                    1, mLocationListener);
            Location yourLocation = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (yourLocation != null) {
                latitude = yourLocation.getLatitude();
                longitude = yourLocation.getLongitude();
            }
        }

        mYourPosition = new LatLng(latitude, longitude);
        mContext = this;

        mMap.addMarker(
                new MarkerOptions()
                        .position(mYourPosition)
                        .title("your position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mYourPosition));
        mMap.setMyLocationEnabled(true);
    }

    @Override
    protected void onPause() {
        stopPermissionDialog();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Obtain permission type required
            int requiredPermissionType = PermissionsHelper.obtainPermissionType(this);
            if (requiredPermissionType == PermissionsHelper.MY_PERMISSIONS_UNKNOWN) {
                mMapFragment.getMapAsync(this);
            }
        }
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        isAskingForPermission = false;
        boolean permissionRequiredGranted = false;
        Activity activity = this;
        if (requestCode == PermissionsHelper.MY_PERMISSIONS_REQUEST_LOCATION_PERMISSION){
            if (grantResults.length > 0 &&
                    PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                Log.v(LOG_TAG, "Location permission granted!!");
                permissionRequiredGranted = true;
            }
        }
        if (permissionRequiredGranted) {
            Log.v(LOG_TAG, "Permission granted!!");
            setYourLocation();
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else {
            Log.v(LOG_TAG, "Permission not granted");

            //Verify if user check on "Never ask again" and take him to application settings to
            //Grant permission manually
            if (PermissionsHelper.isCheckedDontAskAgain(
                    activity, PermissionsHelper.LOCATION_PERMISSION)) {
                if (handleMessage == null) {
                    handleMessage = new PermissionsHelper.HandleMessage();
                    handleMessage.isAskingForPermission(true);
                }
                PermissionsHelper.showPermissionMessage(
                        getString(R.string.permission_from_settings_info),
                        getString(R.string.permission_from_settings_ok_button),
                        getString(R.string.permission_cancel_button),
                        null,
                        activity,
                        PermissionsHelper.MY_PERMISSIONS_UNKNOWN,
                        handleMessage);
            } else {
                stopPermissionDialog();
            }
        }
    }

    private void stopPermissionDialog() {
        if (null != handleMessage) {

            //Close message dialog if is open
            if (handleMessage.getLooper().getThread().isAlive()) {
                //Ensures thread is still alive
                handleMessage.sendEmptyMessage(0);
                handleMessage.isAskingForPermission(false);
            }
            isAskingForPermission = false;
        }
    }
}
